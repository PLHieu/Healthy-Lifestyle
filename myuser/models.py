from django.db import models
from django.contrib.auth.models import AbstractUser, BaseUserManager

from .managers import MyUserManager

class MyUser(AbstractUser):
    
    username = models.CharField(max_length=10, unique=True)
    password = models.CharField(max_length=100)
    email = models.EmailField(max_length=100,null=True)
    name = models.CharField(max_length=50,default=None,null=True)
    tuoi = models.IntegerField(default=None, null=True)
    diachi = models.CharField(max_length = 100, default=None, null = True)
    profile_pic = models.ImageField(null=True, blank=True)

    USERNAME_FIELD = 'username'
    REQUIRED_FIELDS = []
    # objects = MyUserManager() 

    def __str__(self):
        return self.username

# Create your models here.
class Doctor(models.Model):
    user = models.ForeignKey(MyUser, on_delete=models.CASCADE)
    benhvien = models.CharField(max_length=100)
    chuyennganh = models.CharField(max_length = 100)
    sonamcongtac = models.IntegerField()
    trinhdo = models.CharField(max_length=50)
    def __str__(self):
        return self.user

# Create your models here.
class Patient(models.Model):
    user = models.ForeignKey(MyUser, on_delete=models.CASCADE, related_name='%(class)s_bn')
    tenbenh = models.CharField(max_length = 100)
    thoigiandieutri = models.IntegerField()
    bacsiquanly = models.ForeignKey(MyUser, on_delete=models.CASCADE, related_name='%(class)s_bsql')
    def __str__(self):
        return self.user

