from django.db import models
from myuser.models import MyUser

class Run(models.Model):
    user = models.ForeignKey(MyUser, on_delete=models.CASCADE, null=True)
    type = models.IntegerField()
    day = models.IntegerField()
    month = models.IntegerField()
    year = models.IntegerField()
    target = models.IntegerField()
    distance = models.IntegerField()
    timeStart = models.CharField(max_length=20)
    runningTime = models.IntegerField()
    routeID = models.CharField(max_length=20)
    isVisible = models.IntegerField(default=0)
    updated = models.IntegerField(default =1)

    def __str__(self):
        return "hieumap"

    class Meta:
        unique_together = ['user', 'timeStart']




