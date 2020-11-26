package com.example.awesomehabit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.example.awesomehabit.meal.MealActivity;
import com.example.awesomehabit.running.demo;
import com.example.awesomehabit.sleeping.SleepTracker;

public class CustomPageAdapter extends PagerAdapter implements View.OnClickListener {
    private Context mContext;
    public CustomPageAdapter(Context mContext) {
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        return 1000;
    }
    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }
    /*
     * Create the page for the given position.  The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * {@link #finishUpdate(ViewGroup)}.
     *
     * @param container The containing View in which the page will be shown.
     * @param position  The page position to be instantiated.
     * @return Returns an Object representing the new page.  This does not
     * need to be a View, but can be some other container of the page.
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        //ModelObject modelObject = ModelObject.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.card_list, collection, false);
        //TextView tv=layout.findViewById(R.id.tvPos);
        //tv.setText(String.valueOf(position));

        Button btnRun=(Button)layout.findViewById(R.id.startRunning);
        Button btnSleep=(Button)layout.findViewById(R.id.startSleeping);
        Button btnMeal=(Button)layout.findViewById(R.id.btnMeal);
        btnRun.setOnClickListener(this);
        btnSleep.setOnClickListener(this);
        btnMeal.setOnClickListener(this);
        collection.addView(layout);
        return layout;
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.startRunning){
            Intent intent=new Intent(mContext, demo.class);
            mContext.startActivity(intent);
        }
        else if(v.getId()==R.id.startSleeping){
            Intent intent=new Intent(mContext, SleepTracker.class);
            mContext.startActivity(intent);
        }
        else if(v.getId()==R.id.btnMeal){
            Intent intent=new Intent(mContext, MealActivity.class);
            mContext.startActivity(intent);
        }
    }
}
