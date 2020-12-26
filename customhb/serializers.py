from rest_framework import serializers
from .models import CustomHabit

class CTHBSerializer(serializers.ModelSerializer):

    class Meta:
        model = CustomHabit
        fields = ['habitID', 'name', 'type']
