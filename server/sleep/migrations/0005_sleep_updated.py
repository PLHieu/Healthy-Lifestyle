# Generated by Django 3.1.4 on 2021-01-20 16:39

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('sleep', '0004_sleep_isvisible'),
    ]

    operations = [
        migrations.AddField(
            model_name='sleep',
            name='updated',
            field=models.IntegerField(default=1),
        ),
    ]