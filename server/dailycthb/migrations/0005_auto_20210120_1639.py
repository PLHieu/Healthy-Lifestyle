# Generated by Django 3.1.4 on 2021-01-20 16:39

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('dailycthb', '0004_dailycustomhabit_isvisible'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='dailycustomhabit',
            name='time',
        ),
        migrations.AddField(
            model_name='dailycustomhabit',
            name='updated',
            field=models.IntegerField(default=1),
        ),
    ]