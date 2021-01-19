from django.urls import path
from . import views
from rest_framework_simplejwt import views as jwt_views

app_name = 'users'

urlpatterns = [
    path('signup/patient/', views.PatientRegisterView.as_view(), name='patientregister'),
    path('signup/doctor/', views.DoctorRegisterView.as_view(), name='doctorregister'),
    path('signin/', views.UserLoginView.as_view(), name='login'),
    path('update/patient/', views.PatientUpdate.as_view(), name='patientupdate'),
    path('update/doctor/', views.DoctorUpdate.as_view(), name='doctorupdate'),
    path('update_pic/', views.UpdateProfilePic.as_view(), name='updatepic')
]