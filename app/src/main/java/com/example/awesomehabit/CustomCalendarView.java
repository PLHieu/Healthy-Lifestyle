package com.example.awesomehabit;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.Calendar;
import java.util.zip.Inflater;

public class CustomCalendarView extends RecyclerView implements DaySelectButtonAdapter.OnDaySelectListener{
    public final static int NUMBER_OF_DAY_BUTTONS=1000;//Doesn't affect performance!
    public static Calendar currentDay=Calendar.getInstance();
    public static int currentDay_Day=currentDay.get(Calendar.DAY_OF_MONTH);
    public static int currentDay_Month=currentDay.get(Calendar.MONTH);
    public static int currentDay_Year=currentDay.get(Calendar.YEAR);

    private  SnapHelper snapHelper = new PagerSnapHelper();
    private Context context;
    final LinearLayoutManager mLayoutManager;

    public void setResponder(CustomCalendarViewInterface responder) {
        this.responder = responder;
    }

    private CustomCalendarViewInterface responder;

    public final DaySelectButtonAdapter adapter;
    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        adapter = new DaySelectButtonAdapter(context,this);
        setAdapter(adapter);
        //LayoutManager
        mLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        setLayoutManager(mLayoutManager);

        scrollToPosition(NUMBER_OF_DAY_BUTTONS/2-2);
        currentDay_Day=currentDay.get(Calendar.DAY_OF_MONTH);
        currentDay_Month=currentDay.get(Calendar.MONTH);
        currentDay_Year=currentDay.get(Calendar.YEAR);
        //currentDay.set(Calendar.HOUR,12);
        //currentDay.set(Calendar.MINUTE,0);
        //currentDay.set(Calendar.SECOND,0);
        //currentDay.set(Calendar.MILLISECOND,0);
        snapHelper.attachToRecyclerView(this);
    }

    @Override
    public void onDaySelectCLick(int position) {
        //mLayoutManager.scrollToPositionWithOffset(position,getWidth()/2-mLayoutManager.findViewByPosition(position).getWidth()/2);
        smoothScrollTo(position);
        responder.onDaySelected(position);
    }
    public void smoothScrollTo(int position){
        adapter.setCheckedPosition(position);
        LinearLayout t=findViewById(R.id.calendar_parent);
        mLayoutManager.scrollToPositionWithOffset(position,this.getWidth()/2-t.getWidth()/2);

        currentDay=Calendar.getInstance();
        currentDay.add(Calendar.DATE,position-NUMBER_OF_DAY_BUTTONS/2);

        currentDay_Day=currentDay.get(Calendar.DAY_OF_MONTH);
        currentDay_Month=currentDay.get(Calendar.MONTH);
        currentDay_Year=currentDay.get(Calendar.YEAR);
        //currentDay.set(Calendar.HOUR,12);
        //currentDay.set(Calendar.MINUTE,0);
        //currentDay.set(Calendar.SECOND,0);
        //currentDay.set(Calendar.MILLISECOND,0);
    }
    public interface CustomCalendarViewInterface
    {
        void onDaySelected(int position);
    }
}
