from django.db import models
from myuser.models import MyUser

class CustomHabit(models.Model):
    user = models.ForeignKey(MyUser, on_delete=models.CASCADE)
    habitID = models.IntegerField()
    name = models.CharField(max_length=20)
    type = models.IntegerField()

    class Meta:
        unique_together = ['habitID', 'user']