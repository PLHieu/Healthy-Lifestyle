from django.db import models
class Goal(models.Model):
    type = models.IntegerField()
    target = models.IntegerField()

