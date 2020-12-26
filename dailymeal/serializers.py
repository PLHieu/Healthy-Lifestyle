from rest_framework import serializers
from .models import DailyMeal

class DailyMealSerializer(serializers.ModelSerializer):

    class Meta:
        model = DailyMeal
        fields = ['type', 'day', 'month', 'year', 'target', 'calo']
