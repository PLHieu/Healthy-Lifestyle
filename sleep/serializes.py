from rest_framework import serializers
from .models import Sleep

class SleepSerializer(serializers.ModelSerializer):

    class Meta:
        model = Sleep
        fields = ['type', 'day', 'month', 'year', 'target', 'starttime', 'endtime', 'quality']
