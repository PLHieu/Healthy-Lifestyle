from django.urls import path
from . import views
from rest_framework_simplejwt import views as jwt_views

app_name = 'users'

urlpatterns = [
    path('signup/', views.UserRegisterView.as_view(), name='register'),
    path('signin/', views.UserLoginView.as_view(), name='login'),
]