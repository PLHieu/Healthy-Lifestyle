# Generated by Django 3.1.4 on 2021-01-20 16:39

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('dailymeal', '0008_dailymeal_isvisible'),
    ]

    operations = [
        migrations.AddField(
            model_name='dailymeal',
            name='updated',
            field=models.IntegerField(default=1),
        ),
    ]
