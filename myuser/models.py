from django.db import models
from django.contrib.auth.models import AbstractUser, BaseUserManager

from .managers import MyUserManager

class MyUser(AbstractUser):
    
    username = models.CharField(max_length=10, unique=True)
    password = models.CharField(max_length=100)
    email = models.EmailField(max_length=100,default='',null=True)
    name = models.CharField(max_length=50,default='',null=True)
    tuoi = models.IntegerField(default=0, null=True)
    diachi = models.CharField(max_length = 100, default='', null = True)
    profile_pic = models.TextField(null=True, blank=True)
    gioitinh = models.IntegerField(default = '0')
    ngaysinh = models.CharField(max_length = 20, default = '' )
    

    USERNAME_FIELD = 'username'
    REQUIRED_FIELDS = []
    # objects = MyUserManager() 

    def __str__(self):
        return self.username

# Create your models here.
class Doctor(models.Model):
    user = models.ForeignKey(MyUser, on_delete=models.CASCADE, primary_key = True)
    benhvien = models.CharField(max_length=100,default='', null = True)
    chuyennganh = models.CharField(max_length = 100, default='',null = True)
    sonamcongtac = models.IntegerField(default=0, null = True)
    trinhdo = models.CharField(max_length=50,default='', null = True)
    def __str__(self):
        return self.user

# Create your models here.
class Patient(models.Model):
    user = models.ForeignKey(MyUser, on_delete=models.CASCADE, related_name='%(class)s_bn', primary_key = True)
    tenbenh = models.CharField(max_length = 100,default='', null = True)
    thoigiandieutri = models.IntegerField(default=0, null = True)
    bacsiquanly = models.ForeignKey(MyUser, on_delete=models.CASCADE, related_name='%(class)s_bsql') # related name de cho tranh bi xung dot
    def __str__(self):
        return self.user


class Visible(models.Model):
    username = models.CharField(max_length = 15, unique = True, default = "")
    visiRun = models.IntegerField(default = 0)
    visiSleep = models.IntegerField(default = 0)
    visiMeal = models.IntegerField(default = 0)




