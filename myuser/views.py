from django.contrib.auth import authenticate
from django.shortcuts import render
from django.contrib.auth.hashers import make_password
from django.http import HttpResponse, JsonResponse

from rest_framework import status
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated

from finalBackend import settings
from . import serializers
from rest_framework_simplejwt.serializers import TokenObtainPairSerializer
from .models import Doctor, Patient


class DoctorRegisterView(APIView):
    def post(self, request):
        
        # seri data 
        seri = serializers.UserSerializer(data=request.data)
        if seri.is_valid():
            seri.validated_data['password'] = make_password(seri.validated_data['password'])
            new_user = seri.save()
            Doctor.objects.create(user = new_user)

            # Gui ve Token 
            refresh = TokenObtainPairSerializer.get_token(new_user)
            data = {
                'refresh_token': str(refresh),
                'access_token': str(refresh.access_token),
                'access_expires': int(settings.SIMPLE_JWT['ACCESS_TOKEN_LIFETIME'].total_seconds()),
                'refresh_expires': int(settings.SIMPLE_JWT['REFRESH_TOKEN_LIFETIME'].total_seconds())
            }
            return Response(data, status=status.HTTP_201_CREATED)
        else:
            print(seri.errors)
            return JsonResponse({
                'error_message': 'Error Serialize User',
                'errors_code': 400,
            }, status=status.HTTP_400_BAD_REQUEST)


class PatientRegisterView(APIView):
    permission_classes = (IsAuthenticated,)

    def post(self, request):
        print(request.data)
        # lay thogn tin bac si
        bacsiquanly = request.user

        # seri data 
        seri = serializers.UserSerializer(data=request.data)
        if seri.is_valid():
            seri.validated_data['password'] = make_password(seri.validated_data['password'])
            new_user = seri.save() # tao user
            Patient.objects.create(user = new_user, bacsiquanly = bacsiquanly) # tao benh nhan tuong ung
            
            # tao visible 
            Visible.objects.create(username = new_user.username,visiRun = 0 ,visiSleep = 0, visiMeal = 0)

            # tra ve danh sach cac benh nhan
            patients = Patient.objects.filter(bacsiquanly = bacsiquanly)
            patients_user = [p.user for p in patients]

            # Thay ve tra ve benh nhan thi se tra ve user
            patientsSeri = serializers.UserSerializer(patients_user, many = True)


            return JsonResponse({'data': patientsSeri.data},status=status.HTTP_201_CREATED, safe = False)
        else:
            print(seri.errors)
            return JsonResponse({
                'error_message': 'Error Serialize User',
                'errors_code': 400,
            }, status=status.HTTP_400_BAD_REQUEST)

class UserLoginView(APIView):
    def post(self, request):
        serializer = serializers.UserLoginSerializer(data=request.data)
        if serializer.is_valid():
            user = authenticate(
                request,
                username=serializer.validated_data['username'],
                password=serializer.validated_data['password']
            )
            if user:
                refresh = TokenObtainPairSerializer.get_token(user)
                data = {
                    'refresh_token': str(refresh),
                    'access_token': str(refresh.access_token),
                    'access_expires': int(settings.SIMPLE_JWT['ACCESS_TOKEN_LIFETIME'].total_seconds()),
                    'refresh_expires': int(settings.SIMPLE_JWT['REFRESH_TOKEN_LIFETIME'].total_seconds()),
                    'username': user.username,
                    'email': user.email,
                    'name': user.name,
                    'tuoi': user.tuoi,
                    'diachi': user.diachi,
                    'gioitinh': user.gioitinh,
                    'ngaysinh': user.ngaysinh
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


class DoctorUpdate(APIView):
    permission_classes = (IsAuthenticated,)
    def put(self, request):
        currentUser = request.user
        currentDoctor = Doctor.objects.get(user = currentUser)
        seriUser  = serializers.UserSerializer(currentUser, data = request.data)
        seriDoctor = serializers.DoctorSerializer(currentDoctor,data = request.data)
        if seriDoctor.is_valid() and seriUser.is_valid():
            seriDoctor.save()
            seriUser.save()
            return Response("Update Doctor Sucessfully", status=status.HTTP_200_OK)
        else :
            return Response({
                'error_message': 'Update Doctor Failed',
                'error_code': 400
            }, status=status.HTTP_400_BAD_REQUEST)


class PatientUpdate(APIView):
    permission_classes = (IsAuthenticated,)
    def put(self,request):
        currentUser = request.user
        currentPatient = Patient.objects.get(user = currentUser)
        seriUser  = serializers.UserSerializer(currentUser, data = request.data)
        seriPatient = serializers.PatientSerializer(currentPatient, data = request.data)
        if(seriPatient.is_valid() and seriUser.is_valid()  ):
            seriUser.save()
            seriPatient.save()
            return Response("Update Patient Sucessfully", status=status.HTTP_200_OK)
        else :
            print(seriUser.errors)
            print(seriPatient.errors)
            return Response({
                'error_message': 'Update Patient Failed',
                'error_code': 400
            }, status=status.HTTP_400_BAD_REQUEST)
