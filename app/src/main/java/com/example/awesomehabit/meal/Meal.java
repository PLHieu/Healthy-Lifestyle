package com.example.awesomehabit.meal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Meal {
    String name;
    int calories;

    public Meal(String name, int calories) {
        this.name = name;
        this.calories = calories;
        this.bitmap=null;
    }
    String bitmap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public Bitmap getBitmap() {
        return getBitmapFromString(bitmap);
    }

    public void setBitmap(Bitmap bitmapString) {
        this.bitmap = getStringFromBitmap(bitmapString);
    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }
    private Bitmap getBitmapFromString(String stringPicture) {
        if(stringPicture!=null){
            byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedByte;
        }
       return null;
    public int gettime(){
        return 0; // todo: add
    }
}
