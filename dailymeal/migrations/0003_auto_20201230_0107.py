# Generated by Django 3.1.4 on 2020-12-30 01:07

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('dailymeal', '0002_auto_20201226_1213'),
    ]

    operations = [
        migrations.AlterField(
            model_name='dailymeal',
            name='calo',
            field=models.FloatField(null=True),
        ),
    ]
