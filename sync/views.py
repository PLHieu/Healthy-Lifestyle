from django.http import JsonResponse

from rest_framework import status
from rest_framework.views import APIView
from rest_framework.renderers import JSONRenderer

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

    def put(self, request):
        pass

    def get(self, request):
        user = MyUser.objects.get(email="a@gmail.com")
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
            'run' : JSONRenderer().render(runSeri.data),
            'sleep' : JSONRenderer().render(sleepSeri.data),
            'dailymeal' : JSONRenderer().render(mealSeri.data),
            'customhb' : JSONRenderer().render(hbSeri.data),
            'DLcustomhb' : JSONRenderer().render(dlhbSeri.data)
        }, status=status.HTTP_200_OK)


