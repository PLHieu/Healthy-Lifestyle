from django.db import models
from myuser.models import MyUser


class DailyMeal(models.Model):
    user = models.ForeignKey(MyUser, on_delete=models.CASCADE, null=True)
    type = models.IntegerField()
    day = models.IntegerField()
    month = models.IntegerField()
    year = models.IntegerField()
    target = models.IntegerField()
    class Meta:
        unique_together = ['user', 'day', 'month', 'year']

class Meal(models.Model):
    dailymeal = models.ForeignKey(DailyMeal, related_name='mealList', on_delete=models.CASCADE, null = True)
    bitmap = models.TextField(default = '')
    calories = models.IntegerField(max_length = 10, default = 0) 
    name = models.CharField(max_length = 10, default = '')



