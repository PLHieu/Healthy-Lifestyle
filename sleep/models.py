from django.db import models
from myuser.models import MyUser

class Sleep(models.Model):
    user = models.ForeignKey(MyUser, on_delete=models.CASCADE)
    type = models.IntegerField()
    day = models.IntegerField()
    month = models.IntegerField()
    year = models.IntegerField()
    target = models.IntegerField()
    starttime = models.CharField(max_length=20)
    endtime = models.CharField(max_length=20)
    quality = models.IntegerField()

    class Meta:
        unique_together = ['user', 'starttime']