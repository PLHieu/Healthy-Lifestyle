from django.contrib.auth import authenticate
from django.shortcuts import render
from django.contrib.auth.hashers import make_password
from django.http import HttpResponse, JsonResponse

from rest_framework import status
from rest_framework.views import APIView
from rest_framework.response import Response

from . import serializers
from rest_framework_simplejwt.serializers import TokenObtainPairSerializer


class UserRegisterView(APIView):

    def post(self, request):
        serializer = serializers.UserSerializer(data=request.data)
        if serializer.is_valid():
            serializer.validated_data['password'] = make_password(serializer.validated_data['password'])
            user = serializer.save()

            return JsonResponse({
                'message': 'Register successful!'
            }, status=status.HTTP_201_CREATED)

        else:
            return JsonResponse({
                'error_message': 'This email has already exist!',
                'errors_code': 400,
            }, status=status.HTTP_400_BAD_REQUEST)

class UserLoginView(APIView):
    def post(self, request):

        print(request.data)
        print("hieumao")
        serializer = serializers.UserLoginSerializer(data=request.data)
        if serializer.is_valid():
            user = authenticate(
                request,
                username=serializer.validated_data['email'],
                password=serializer.validated_data['password']
            )
            if user:
                refresh = TokenObtainPairSerializer.get_token(user)
                data = {
                    'refresh_token': str(refresh),
                    'access_token': str(refresh.access_token)
                }
                return Response(data, status=status.HTTP_200_OK)

            return Response({
                'error_message': 'Email or password is incorrect!',
                'error_code': 400
            }, status=status.HTTP_400_BAD_REQUEST)

        return Response({
            'error_messages': serializer.errors,
            'error_code': 400
        }, status=status.HTTP_400_BAD_REQUEST)
