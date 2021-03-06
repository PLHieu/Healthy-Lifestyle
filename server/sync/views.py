from django.db import transaction
from django.http import JsonResponse

from rest_framework import status
from rest_framework.parsers import JSONParser
from rest_framework.utils import json
from rest_framework.views import APIView
from rest_framework.renderers import JSONRenderer
from rest_framework.permissions import IsAuthenticated

from myuser.models import MyUser, Patient, Visible
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
from myuser.serializers import UserSerializer
from goal.serializers import GoalSerializer

class SyncListPatient(APIView):
    permission_classes = (IsAuthenticated,)

    def get(self, request):
        # lay thong tin bac si
        bacsiquanly = request.user

        # tra ve danh sach cac benh nhan
        patients = Patient.objects.filter(bacsiquanly = bacsiquanly)
        patients_user = [p.user for p in patients]

        # Thay ve tra ve benh nhan thi se tra ve user
        patientsSeri = UserSerializer(patients_user, many = True)

        try:
            return JsonResponse({'data': patientsSeri.data},status=status.HTTP_201_CREATED, safe = False)
        except:
            print(patientsSeri.errors)
            return JsonResponse({
                'error_message': 'Error Serialize Patient',
                'errors_code': 400,
            }, status=status.HTTP_400_BAD_REQUEST) 


class DoctorGetHabit(APIView):
    permission_classes = (IsAuthenticated,)

    def post(self, request):

        # lay username benh nhan
        patient_username = request.data.get('username')
        print('hieu map ', patient_username)

        user = MyUser.objects.get(username = patient_username)
        
        visi = Visible.objects.get(username = patient_username)

        # lay habit cua benh nhan
        runs = Run.objects.filter(user=user)
        sleeps = Sleep.objects.filter(user = user)
        meals = DailyMeal.objects.filter(user = user)
        habits = CustomHabit.objects.filter(user = user)
        dlhabits = DailyCustomHabit.objects.filter(user = user)
        goals = Goal.objects.filter(user = user)

        runSeri = RunSerializer(runs, many=True)
        sleepSeri = SleepSerializer(sleeps, many=True)
        mealSeri = DailyMealSerializer(meals, many=True)
        hbSeri = CTHBSerializer(habits, many=True)
        dlhbSeri = DailyCTHBSerializer(dlhabits, many=True)
        goalseri = GoalSerializer(goals, many=True)

        return JsonResponse({
            'run' : runSeri.data,
            'sleep' : sleepSeri.data,
            'dailymeal' : mealSeri.data,
            'customhb' : hbSeri.data,
            'DLcustomhb' : dlhbSeri.data,
            'goal': goalseri.data,
            'visiRun' : visi.visiRun,
            'visiSleep' : visi.visiSleep,
            'visiMeal' : visi.visiMeal,
        }, status=status.HTTP_200_OK)
        #todo: time cua daily customhabit, chua xu li unique    

class PatientPostData(APIView):
    permission_classes = (IsAuthenticated,)

    def put(self, request):
        # lay thong tin benh nhan
        user = request.user
        print(request.data)
        # tu dong parse request duoi dang json
        # parser_classes = [JSONParser]
        # print(user)
        runjs = json.loads(request.data.get("run"))
        sleepjs = json.loads(request.data.get("sleep"))
        mealjs = json.loads(request.data.get("dailymeal"))
        customhbjs = json.loads(request.data.get("customHB"))
        dailycustomhbjs = json.loads(request.data.get("dailycustomHB"))

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
        # print(meals)
        # print(runs)



        if(
            runs.is_valid()
            and sleeps.is_valid()
            and meals.is_valid()
            and hbs.is_valid()
            and dailyhbs.is_valid()
        ):
            

            with transaction.atomic():
                runs.save()
                print("push runs")
                sleeps.save()
                print("push sleep")
                meals.save()
                print("push meal")
                hbs.save()
                print("push hbs")
                dailyhbs.save()
                print("push dailyhb")
        else:
            # print(dailyhbs.is_valid(raise_exception=True))
            print(runs.errors)
            print(sleeps.errors)
            print(meals.errors)
            print(hbs.errors)
            print(dailyhbs.errors)
            return JsonResponse({"Error": "Error Serialize Data"}, status = status.HTTP_400_BAD_REQUEST)

        return JsonResponse({"Push sucessfully" : "HieuPL"}, status = status.HTTP_200_OK)



class DoctorPostData(APIView):
    permission_classes = (IsAuthenticated,)

    def setGoal(self, user, goalsjs):
        for onejs in goalsjs:
            type = onejs.get('type')
            old_goal = Goal.objects.get(type = type, user = user)
            goals = GoalSerializer(instance = old_goal ,data = onejs, context={"owner": user})
            if goals.is_valid():
                goals.save()
            else:
                print(goals.errors)

    def put(self, request):
        # lay thong tin benh nhan
        patient_username = request.data.get('username')

        # neu nhu trong bang visible khong co thi create, con khong thi se update
        visiRun = request.data.get('visiRun')
        visiSleep = request.data.get('visiSleep')
        visiMeal = request.data.get('visiMeal')
        try:
            visi = Visible.objects.get(username = patient_username)
            visi.visiRun = visiRun
            visi.visiSleep = visiSleep
            visi.visiMeal = visiMeal
            visi.save()

        except Visible.DoesNotExist:
            Visible.objects.create(username = patient_username,visiRun = visiRun ,visiSleep = visiSleep, visiMeal = visiMeal)
        

        user = MyUser.objects.get(username = patient_username)

        runjs = json.loads(request.data.get("run"))
        sleepjs = json.loads(request.data.get("sleep"))
        mealjs = json.loads(request.data.get("dailymeal"))
        customhbjs = json.loads(request.data.get("customHB"))
        dailycustomhbjs = json.loads(request.data.get("dailycustomHB"))
        goalsjs = json.loads(request.data.get("goal"))

        runs = RunSerializer(data=runjs, many=True, context= {"owner": user})
        sleeps = SleepSerializer(data=sleepjs, many=True, context= {"owner": user})
        meals = DailyMealSerializer(data=mealjs, many=True, context= {"owner": user})
        hbs = CTHBSerializer(data=customhbjs, many=True, context= {"owner": user})
        dailyhbs = DailyCTHBSerializer(data=dailycustomhbjs, many=True, context= {"owner": user})
        # goals = GoalSerializer(data = goalsjs, many = True, context={"owner": user})
        self.setGoal(user, goalsjs)

        if(
            runs.is_valid()
            and sleeps.is_valid()
            and meals.is_valid()
            and hbs.is_valid()
            and dailyhbs.is_valid()
            # and goals.is_valid()\
        ):
            

            with transaction.atomic():
                runs.save()
                print("push runs")
                sleeps.save()
                print("push sleep")
                meals.save()
                print("push meal")
                hbs.save()
                print("push hbs")
                dailyhbs.save()
                print("push dailyhb")
                # goals.save()
                # print("push goal")
        else:
            # print(dailyhbs.is_valid(raise_exception=True))
            print(runs.errors)
            print(sleeps.errors)
            print(meals.errors)
            print(hbs.errors)
            print(dailyhbs.errors)
            return JsonResponse({"Error": "Error Serialize Data"}, status = status.HTTP_400_BAD_REQUEST)

        return JsonResponse({"Push sucessfully" : "HieuPL"}, status = status.HTTP_200_OK)