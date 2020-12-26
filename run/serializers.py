from rest_framework import serializers

from .models import Run

class RunSerializer(serializers.ModelSerializer):

    class Meta:
        model = Run
        fields = ['type', 'day', 'month', 'year', 'target', 'distance', 'timeStart', 'runningtime', 'route']
