from rest_framework import serializers
from .models import DailyMeal, Meal

class MealSerializer(serializers.ModelSerializer):
    class Meta:
        model = Meal
        fields = ['bitmap', 'calories', 'name']

class DailyMealSerializer(serializers.ModelSerializer):
    mealList = MealSerializer(many=True) 

    def update(self, instance, validated_data):
        instance = super(DailyMealSerializer, self).update(instance, validated_data)
        if("owner" in self.context):
            instance.user = self.context["owner"]
        instance.save()
        return instance

    def create(self, validated_data):

        meallistdata = validated_data.pop('mealList')
        obj = DailyMeal.objects.create(**validated_data)
        if ("owner" in self.context):
            obj.user = self.context["owner"]
            obj.updated =1
            obj.save()
        for mealdata in meallistdata:
            Meal.objects.create(dailymeal = obj, **mealdata)
        
        
        return obj

    class Meta:
        model = DailyMeal
        
        fields = ['type', 'day', 'month', 'year', 'target', 'mealList', 'updated']

