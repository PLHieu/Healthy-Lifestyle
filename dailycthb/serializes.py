from rest_framework import serializers
from .models import DailyCustomHabit

class DailyCTHBSerializer(serializers.ModelSerializer):

    HabitID_ = serializers.IntegerField(source='HabitID')

    def update(self, instance, validated_data):
        print("onupdate")
        instance = super(DailyCTHBSerializer, self).update(instance, validated_data)
        if("owner" in self.context):
            instance.user = self.context["owner"]
            print("add user attr")
        else:
            print("cannot add user attr")
        instance.save()
        return instance

    def create(self, validated_data):
        
        print("oncreate")
        obj = DailyCustomHabit.objects.create(**validated_data)

        if ("owner" in self.context):
            obj.user = self.context["owner"]
        else:
            print("ngay xua co co be treo cay me")
        obj.save()
        return obj

    class Meta:
        model = DailyCustomHabit
        fields = ['HabitID_', 'current', 'target', 'day', 'month', 'year']
