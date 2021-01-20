from django.db import models
from myuser.models import MyUser

class DailyCustomHabit(models.Model):
    user = models.ForeignKey(MyUser, on_delete=models.CASCADE, null=True)
    HabitID = models.IntegerField()
    current = models.IntegerField()
    target = models.IntegerField()
    day = models.IntegerField()
    month = models.IntegerField()
    year = models.IntegerField()
    # time = models.CharField(max_length= 25)
    isVisible = models.IntegerField(default=0)
    class Meta:
        unique_together = ['HabitID', 'user']