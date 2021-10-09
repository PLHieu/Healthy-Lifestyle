# Generated by Django 3.1.4 on 2020-12-26 06:54

from django.conf import settings
from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('dailycthb', '0002_auto_20201226_1213'),
    ]

    operations = [
        migrations.RenameField(
            model_name='dailycustomhabit',
            old_name='habitID',
            new_name='HabitID',
        ),
        migrations.AlterUniqueTogether(
            name='dailycustomhabit',
            unique_together={('HabitID', 'user')},
        ),
    ]
