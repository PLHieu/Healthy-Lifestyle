from django.db import models
from myuser.models import MyUser


class Goal(models.Model):
    user = models.ForeignKey(MyUser, on_delete=models.CASCADE, null=True)
    type = models.IntegerField()
    target = models.IntegerField()

    class Meta:
        unique_together = ['user', 'type']