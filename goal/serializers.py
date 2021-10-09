from rest_framework import serializers
from .models import Goal

class GoalSerializer(serializers.ModelSerializer):
    def update(self, instance, validated_data):
        instance = super(GoalSerializer, self).update(instance, validated_data)
        if("owner" in self.context):
            instance.user = self.context["owner"]
        instance.save()
        return instance

    def create(self, validated_data):
        obj = Goal.objects.create(**validated_data)
        if ("owner" in self.context):
            obj.user = self.context["owner"]
        obj.save()
        return obj

    class Meta:
        model = Goal
        fields = ('type', 'target')