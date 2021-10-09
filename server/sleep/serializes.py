from rest_framework import serializers
from .models import Sleep

class SleepSerializer(serializers.ModelSerializer):

    def update(self, instance, validated_data):
        instance = super(SleepSerializer, self).update(instance, validated_data)
        if("owner" in self.context):
            instance.user = self.context["owner"]
        instance.save()
        return instance

    def create(self, validated_data):
        obj = Sleep.objects.create(**validated_data)
        if ("owner" in self.context):
            obj.user = self.context["owner"]
        obj.updated =1
        obj.save()
        return obj

    class Meta:
        model = Sleep
        fields = ['type', 'day', 'month', 'year', 'target', 'startTimeMilli', 'endTimeMilli', 'sleepQuality', 'updated']
