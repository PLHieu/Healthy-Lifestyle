from django.db import transaction
from django.http import JsonResponse

from rest_framework import status
from rest_framework.parsers import JSONParser
from rest_framework.utils import json
from rest_framework.views import APIView
from rest_framework.renderers import JSONRenderer
from rest_framework.permissions import IsAuthenticated

from myuser.models import MyUser
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


class MySync(APIView):
    permission_classes = (IsAuthenticated,)

    def put(self, request):
        print(request)
        # tu dong parse request duoi dang json
        parser_classes = [JSONParser]
        user = request.user

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
        print(dailycustomhbjs)

        if(
            runs.is_valid(raise_exception=True)
            and sleeps.is_valid(raise_exception=True)
            and meals.is_valid(raise_exception=True)
            and hbs.is_valid()
            and dailyhbs.is_valid()\
        ):
            print(dailyhbs.errors)
            with transaction.atomic():
                runs.save()
                sleeps.save()
                meals.save()
                hbs.save()
                dailyhbs.save()
        else:
            # print(dailyhbs.errors)
            return JsonResponse({"Error": "Error when parse data"}, status = status.HTTP_400_BAD_REQUEST)

        return JsonResponse({"hieu" : "cc"}, status = status.HTTP_200_OK)

    def get(self, request):
        user = request.user
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