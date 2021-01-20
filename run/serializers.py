from rest_framework import serializers

from .models import Run

class RunSerializer(serializers.ModelSerializer):

    def update(self, instance, validated_data):
        instance = super(RunSerializer, self).update(instance, validated_data)
        if("owner" in self.context):
            instance.user = self.context["owner"]
        instance.save()
        return instance

    def create(self, validated_data):
        obj = Run.objects.create(**validated_data)
        if ("owner" in self.context):
            obj.user = self.context["owner"]
        obj.save()
        return obj

    class Meta:
        model = Run
        fields = ['type', 'day', 'month', 'year', 'target', 'distance', 'timeStart', 'runningTime', 'routeID', 'isVisible']
        extra_kwargs = {
            'routeID': {
                'required': False,
                'allow_blank': True,
            }
        }