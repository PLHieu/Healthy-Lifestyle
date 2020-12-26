from rest_framework import serializers

from .models import Run

class RunSerializer(serializers.ModelSerializer):

    def update(self, instance, validated_data):
        # MANIPULATE DATA HERE BEFORE INSERTION
        instance = super(RunSerializer, self).update(instance, validated_data)
        # ADD CODE HERE THAT YOU WANT TO VIEW
        if("owner" in self.context):
            instance.user = self.context["owner"]
            print("hieu map")
        else:
            print("ngay xua co co be treo cay me")
        instance.save()
        return instance

    def create(self, validated_data):
        obj = Run.objects.create(**validated_data)
        if ("owner" in self.context):
            obj.user = self.context["owner"]
            print("hieu map")
        else:
            print("ngay xua co co be treo cay me")
        obj.save()
        return obj

    class Meta:
        model = Run
        fields = ['type', 'day', 'month', 'year', 'target', 'distance', 'timeStart', 'runningTime', 'routeID']
        extra_kwargs = {
            'routeID': {
                'required': False,
                'allow_blank': True,
            }
        }