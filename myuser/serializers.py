from rest_framework import serializers
from django.contrib.auth.hashers import make_password
from .models import MyUser, Doctor, Patient


# cai nay dung cho viec dang ky doi voi Bac Si
class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = MyUser
        fields = ('username', 'password', 'email', 'name', 'tuoi', 'diachi')
        extra_kwargs = {'password': {'write_only': True}}
    def update(self, instance, validated_data):
        validated_data['password'] = make_password(validated_data['password'])
        instance = super(UserSerializer, self).update(instance, validated_data)
        return instance
    
# dung cho dang nhap
class UserLoginSerializer(serializers.Serializer):
    username = serializers.CharField(required=True)
    password = serializers.CharField(required=True)


class DoctorSerializer(serializers.ModelSerializer):
    class Meta:
        fields = ('benhvien','chuyennganh','sonamcongtac','trinhdo')
        model = Doctor

class PatientSerializer(serializers.ModelSerializer):
    class Meta:
        model = Patient
        fields = ('tenbenh', 'thoigiandieutri')

class NewPatientSerializer(serializers.ModelSerializer):
    user = UserSerializer()
    class Meta:
        model = Patient
        fields = ('user','tenbenh', 'thoigiandieutri')