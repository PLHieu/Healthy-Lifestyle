package com.example.awesomehabit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.Calendar;
import java.util.zip.Inflater;

public class CustomCalendarView extends RecyclerView implements DaySelectButtonAdapter.OnDaySelectListener{
    public final static int NUMBER_OF_DAY_BUTTONS=1000;//Doesn't affect performance!
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
        //mLayoutManager.scrollToPositionWithOffset(position,this.getWidth()/2-t.getWidth()/2);
        mLayoutManager.scrollToPositionWithOffset(position,0);
    }
    public interface CustomCalendarViewInterface
    {
        void onDaySelected(int position);
    }
}
