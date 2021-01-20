from django.db import models
from myuser.models import MyUser

class CustomHabit(models.Model):
    user = models.ForeignKey(MyUser, on_delete=models.CASCADE, null=True)
    HabitID = models.IntegerField()
    name = models.CharField(max_length=20)
    type = models.IntegerField()
    iconID = models.CharField(max_length = 20, default = '')
    isVisible = models.IntegerField(default=0)
    updated = models.IntegerField(default =1)

    class Meta:
        unique_together = ['HabitID', 'user']