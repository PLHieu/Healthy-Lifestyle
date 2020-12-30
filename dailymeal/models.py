from django.db import models
from myuser.models import MyUser
class DailyMeal(models.Model):
    user = models.ForeignKey(MyUser, on_delete=models.CASCADE, null=True)
    type = models.IntegerField()
    day = models.IntegerField()
    month = models.IntegerField()
    year = models.IntegerField()
    target = models.FloatField()
    calo = models.FloatField(null = True)

    class Meta:
        unique_together = ['user', 'day', 'month', 'year']