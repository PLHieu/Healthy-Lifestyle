from rest_framework import serializers
from .models import DailyMeal

class DailyMealSerializer(serializers.ModelSerializer):

    def update(self, instance, validated_data):
        # MANIPULATE DATA HERE BEFORE INSERTION
        instance = super(DailyMealSerializer, self).update(instance, validated_data)
        # ADD CODE HERE THAT YOU WANT TO VIEW
        if("owner" in self.context):
            instance.user = self.context["owner"]
            print("hieu map")
        else:
            print("ngay xua co co be treo cay me")
        instance.save()
        return instance

    def create(self, validated_data):

        obj = DailyMeal.objects.create(**validated_data)

        if ("owner" in self.context):
            obj.user = self.context["owner"]
            print("hieu map")
        else:
            print("ngay xua co co be treo cay me")
        obj.save()
        return obj

    class Meta:
        model = DailyMeal
        fields = ['type', 'day', 'month', 'year', 'target', 'calo']
