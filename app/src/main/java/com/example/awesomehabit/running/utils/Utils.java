package com.example.awesomehabit.running.utils;

public class Utils {
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static int doubleKmTointMetter(double dis){
        Double r = dis*1000;
        return r.intValue();
    }
}
