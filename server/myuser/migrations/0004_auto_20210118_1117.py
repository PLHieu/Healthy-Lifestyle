# Generated by Django 3.1.4 on 2021-01-18 11:17

import django.contrib.auth.models
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('myuser', '0003_doctor_patient'),
    ]

    operations = [
        migrations.AlterModelManagers(
            name='myuser',
            managers=[
                ('objects', django.contrib.auth.models.UserManager()),
            ],
        ),
        migrations.AddField(
            model_name='myuser',
            name='diachi',
            field=models.CharField(default=None, max_length=100),
        ),
        migrations.AddField(
            model_name='myuser',
            name='name',
            field=models.CharField(default=None, max_length=50),
        ),
        migrations.AddField(
            model_name='myuser',
            name='tuoi',
            field=models.IntegerField(default=None),
        ),
        migrations.AddField(
            model_name='myuser',
            name='username',
            field=models.CharField(default='', max_length=10, unique=True),
            preserve_default=False,
        ),
    ]
