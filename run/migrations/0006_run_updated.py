# Generated by Django 3.1.4 on 2021-01-20 16:39

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('run', '0005_run_isvisible'),
    ]

    operations = [
        migrations.AddField(
            model_name='run',
            name='updated',
            field=models.IntegerField(default=1),
        ),
    ]
