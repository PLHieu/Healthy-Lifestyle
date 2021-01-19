from rest_framework import serializers

from .models import MyUser, Doctor, Patient


# Seri MyUser
class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = MyUser
        fields = ('username', 'password', 'email', 'name', 'tuoi', 'diachi')
        read_only_fields = ('profile_pic', )
        extra_kwargs = {'password': {'write_only': True}}

# dung cho dang ky
class UserSignupSerializer(serializers.Serializer):
    username = serializers.CharField(required=True)
    password = serializers.CharField(required=True)

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

class ProfilePictureSerializer(serializers.ModelSerializer):
    profile_pic = serializers.ImageField(allow_null=True)
    class Meta:
        model = MyUser
        fields = ['profile_pic']