from rest_framework import serializers
from .models import DailyCustomHabit

class DailyCTHBSerializer(serializers.ModelSerializer):

    class Meta:
        model = DailyCustomHabit
        fields = ['habitID', 'current', 'target', 'day', 'month', 'year', 'time']
