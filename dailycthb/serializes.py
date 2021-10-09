from rest_framework import serializers
from .models import DailyCustomHabit

class DailyCTHBSerializer(serializers.ModelSerializer):

    HabitID_ = serializers.IntegerField(source='HabitID')

    def update(self, instance, validated_data):
        instance = super(DailyCTHBSerializer, self).update(instance, validated_data)
        if("owner" in self.context):
            instance.user = self.context["owner"]
        instance.save()
        return instance

    def create(self, validated_data):
        obj = DailyCustomHabit.objects.create(**validated_data)
        if ("owner" in self.context):
            obj.user = self.context["owner"]
        obj.updated =1
        obj.save()
        return obj

    class Meta:
        model = DailyCustomHabit
        fields = ['HabitID_', 'current', 'target', 'day', 'month', 'year', 'updated']
