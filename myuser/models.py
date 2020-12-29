from django.db import models
from django.contrib.auth.models import AbstractUser, BaseUserManager

from .managers import MyUserManager

class MyUser(AbstractUser):
    
    # Delete not use field
    username = None
    password = models.CharField(max_length=100)
    email = models.EmailField(max_length=100, unique=True)

    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = []
    objects = MyUserManager() 

    def __str__(self):
        return self.email






