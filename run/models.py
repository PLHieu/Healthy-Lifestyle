from django.db import models
from myuser.models import MyUser

class Run(models.Model):
    user = models.ForeignKey(MyUser, on_delete=models.CASCADE)
    type = models.IntegerField()
    day = models.IntegerField()
    month = models.IntegerField()
    year = models.IntegerField()
    target = models.IntegerField()
    distance = models.IntegerField()
    timeStart = models.CharField(max_length=20)
    runningtime = models.IntegerField()
    route = models.CharField(max_length=20)

    def __str__(self):
        return "hieumap"

    class Meta:
        unique_together = ['user', 'timeStart']



