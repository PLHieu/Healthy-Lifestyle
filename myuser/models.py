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
    user = models.OneToOneField(MyUser, on_delete=models.CASCADE, primary_key = True)
    benhvien = models.CharField(max_length=100, null = True)
    chuyennganh = models.CharField(max_length = 100, null = True)
    sonamcongtac = models.IntegerField(null = True)
    trinhdo = models.CharField(max_length=50, null = True)
    def __str__(self):
        return self.user

# Create your models here.
class Patient(models.Model):
    user = models.OneToOneField(MyUser, on_delete=models.CASCADE, related_name='%(class)s_bn', primary_key = True)
    tenbenh = models.CharField(max_length = 100, null = True)
    thoigiandieutri = models.IntegerField( null = True)
    bacsiquanly = models.ForeignKey(MyUser, on_delete=models.CASCADE, related_name='%(class)s_bsql') # related name de cho tranh bi xung dot
    def __str__(self):
        return self.user

