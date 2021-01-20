from rest_framework import serializers
from .models import CustomHabit

class CTHBSerializer(serializers.ModelSerializer):

    def update(self, instance, validated_data):
        instance = super(CTHBSerializer, self).update(instance, validated_data)
        if("owner" in self.context):
            instance.user = self.context["owner"]
        instance.save()
        return instance

    def create(self, validated_data):
        obj = CustomHabit.objects.create(**validated_data)
        if ("owner" in self.context):
            obj.user = self.context["owner"]
        obj.save()
        return obj

    class Meta:
        model = CustomHabit
        fields = ['HabitID', 'name', 'type','iconID', 'CHupdated']

