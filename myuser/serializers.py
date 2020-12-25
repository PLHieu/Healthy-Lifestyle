from rest_framework import serializers

from .models import MyUser


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = MyUser
        fields = ('email', 'password')
        extra_kwargs = {'password': {'write_only': True}}

class UserLoginSerializer(serializers.Serializer):
    email = serializers.EmailField(required=True)
    password = serializers.CharField(required=True)