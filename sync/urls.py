from django.urls import path
from . import views
from rest_framework_simplejwt import views as jwt_views

app_name = 'sync'

urlpatterns = [
    path('push/', views.MySync.as_view(), name='push'),
    path('pull/', views.MySync.as_view(), name='pull'),
    path('listpatient/', views.SyncListPatient.as_view(), name = "listpatient"),
]