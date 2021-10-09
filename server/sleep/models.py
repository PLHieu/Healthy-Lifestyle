from django.db import models
from myuser.models import MyUser

class Sleep(models.Model):
    user = models.ForeignKey(MyUser, on_delete=models.CASCADE, null=True)
    type = models.IntegerField()
    day = models.IntegerField()
    month = models.IntegerField()
    year = models.IntegerField()
    target = models.IntegerField()
    startTimeMilli = models.CharField(max_length=20)
    endTimeMilli = models.CharField(max_length=20)
    sleepQuality = models.IntegerField()
    isVisible = models.IntegerField(default=0)
    updated = models.IntegerField(default =1)

    class Meta:
        unique_together = ['user', 'startTimeMilli']