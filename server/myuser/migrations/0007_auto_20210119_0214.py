# Generated by Django 3.1.4 on 2021-01-19 02:14

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('myuser', '0006_auto_20210119_0211'),
    ]

    operations = [
        migrations.AlterField(
            model_name='doctor',
            name='benhvien',
            field=models.CharField(max_length=100, null=True),
        ),
        migrations.AlterField(
            model_name='doctor',
            name='chuyennganh',
            field=models.CharField(max_length=100, null=True),
        ),
        migrations.AlterField(
            model_name='doctor',
            name='sonamcongtac',
            field=models.IntegerField(null=True),
        ),
        migrations.AlterField(
            model_name='doctor',
            name='trinhdo',
            field=models.CharField(max_length=50, null=True),
        ),
        migrations.AlterField(
            model_name='patient',
            name='tenbenh',
            field=models.CharField(max_length=100, null=True),
        ),
        migrations.AlterField(
            model_name='patient',
            name='thoigiandieutri',
            field=models.IntegerField(null=True),
        ),
    ]
