package com.example.awesomehabit;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class HabitCardView extends LinearLayout {
    private CardView cardView;
    private FrameLayout frameLayout;
    public HabitCardView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.habit_card,this);
        cardView=findViewById(R.id.habit_card);
        frameLayout=findViewById(R.id.habit_card_frame);
    }

    public HabitCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.habit_card,this);
        cardView=findViewById(R.id.habit_card);
        frameLayout=findViewById(R.id.habit_card_frame);
    }

    public HabitCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.habit_card,this);
        cardView=findViewById(R.id.habit_card);
        frameLayout=findViewById(R.id.habit_card_frame);
    }

    /**
     * Adds a child view with the specified layout parameters.
     *
     * <p><strong>Note:</strong> do not invoke this method from
     * {@link #draw(Canvas)}, {@link #onDraw(Canvas)},
     * {@link #dispatchDraw(Canvas)} or any related method.</p>
     *
     * @param child  the child view to add
     * @param index  the position at which to add the child or -1 to add last
     * @param params the layout parameters to set on the child
     */
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (frameLayout==null){
            super.addView(child, index, params);
        }
        else{
            frameLayout.addView(child,index,params);
        }

    }
}
