package com.example.awesomehabit;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SleepHabit extends FrameLayout {
    public SleepHabit(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(),R.layout.running_habit,this);
        ImageView imageView=(ImageView)findViewById(R.id.habitIcon);
        imageView.setImageResource(R.drawable.bed);
    }
}
