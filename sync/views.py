from django.db import transaction
from django.http import JsonResponse

from rest_framework import status
from rest_framework.parsers import JSONParser
from rest_framework.utils import json
from rest_framework.views import APIView
from rest_framework.renderers import JSONRenderer
from rest_framework.permissions import IsAuthenticated

from myuser.models import MyUser, Patient
from run.models import Run
from sleep.models import Sleep
from dailymeal.models import DailyMeal
from customhb.models import CustomHabit
from dailycthb.models import DailyCustomHabit
from goal.models import Goal

from run.serializers import RunSerializer
from sleep.serializes import SleepSerializer
from dailymeal.serializers import DailyMealSerializer
from customhb.serializers import CTHBSerializer
from dailycthb.serializes import DailyCTHBSerializer
from myuser.serializers import NewPatientSerializer

class SyncListPatient(APIView):
    permission_classes = (IsAuthenticated,)

    def get(self, request):
        # lay thong tin bac si
        bacsiquanly = request.user

        # tra ve danh sach cac benh nhan
        patients = Patient.objects.filter(bacsiquanly = bacsiquanly)
        patientsSeri = NewPatientSerializer(patients, many = True)

        try:
            return JsonResponse(patientsSeri.data,status=status.HTTP_201_CREATED, safe = False)
        except:
            print(patientsSeri.errors)
            return JsonResponse({
                'error_message': 'Error Serialize Patient',
                'errors_code': 400,
            }, status=status.HTTP_400_BAD_REQUEST) 


class DoctorGetHabit(APIView):
    permission_classes = (IsAuthenticated,)

    def get(self, request):
        # lay username benh nhan
        patient_username = request.data.get('username')
        user = MyUser.objects.filter(username = patient_username)

        # lay habit cua benh nhan
        runs = Run.objects.filter(user=user)
        sleeps = Sleep.objects.filter(user = user)
        meals = DailyMeal.objects.filter(user = user)
        habits = CustomHabit.objects.filter(user = user)
        dlhabits = DailyCustomHabit.objects.filter(user = user)
        # goals = Goal.filter(user = user)

        runSeri = RunSerializer(runs, many=True)
        sleepSeri = SleepSerializer(sleeps, many=True)
        mealSeri = DailyMealSerializer(meals, many=True)
        hbSeri = CTHBSerializer(habits, many=True)
        dlhbSeri = DailyCTHBSerializer(dlhabits, many=True)

        return JsonResponse({
            'run' : runSeri.data,
            'sleep' : sleepSeri.data,
            'dailymeal' : mealSeri.data,
            'customhb' : hbSeri.data,
            'DLcustomhb' : dlhbSeri.data
        }, status=status.HTTP_200_OK)
        #todo: time cua daily customhabit, chua xu li unique    

class PatientPostData(APIView):
    permission_classes = (IsAuthenticated,)

    def put(self, request):
        # lay thong tin benh nhan
        user = request.user

        # print(request)
        # tu dong parse request duoi dang json
        # parser_classes = [JSONParser]
        
        # print(user)
        runjs = json.loads(request.data.get("run"))
        sleepjs = json.loads(request.data.get("sleep"))
        mealjs = json.loads(request.data.get("dailymeal"))
        customhbjs = json.loads(request.data.get("customHB"))
        dailycustomhbjs = json.loads(request.data.get("dailycustomHB"))
        # goalsjs = json.loads(request.get("goal"))

        runs = RunSerializer(data=runjs, many=True, context= {"owner": user})
        sleeps = SleepSerializer(data=sleepjs, many=True, context= {"owner": user})
        meals = DailyMealSerializer(data=mealjs, many=True, context= {"owner": user})
        hbs = CTHBSerializer(data=customhbjs, many=True, context= {"owner": user})
        dailyhbs = DailyCTHBSerializer(data=dailycustomhbjs, many=True, context= {"owner": user})
        # print(dailycustomhbjs)
        # print(dailyhbs.is_valid(raise_exception=True))
        # print(dailyhbs.errors)
        # a = runs.is_valid()
        # b = sleeps.is_valid()
        # c = meals.is_valid()
        # d = hbs.is_valid()
        # e = dailyhbs.is_valid()
        # print(a,b,c,d,e)
        # print(meals.errors)
        if(
            runs.is_valid()
            and sleeps.is_valid()
            and meals.is_valid()
            and hbs.is_valid()
            and dailyhbs.is_valid()\
        ):
            print("tag")

            with transaction.atomic():
                runs.save()
                sleeps.save()
                meals.save()
                hbs.save()
                dailyhbs.save()
        else:
            # print(dailyhbs.is_valid(raise_exception=True))
            print(runs.errors)
            print(sleeps.errors)
            print(meals.errors)
            print(hbs.errors)
            print(dailyhbs.errors)
            return JsonResponse({"Error": "Error Serialize Data"}, status = status.HTTP_400_BAD_REQUEST)

        return JsonResponse({"Push sucessfully" : "HieuPL"}, status = status.HTTP_200_OK)