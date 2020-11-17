package com.example.awesomehabit;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CustomCalendarView extends RecyclerView implements DaySelectButtonAdapter.OnDaySelectListener{
    private Calendar calendar;
    private  SnapHelper snapHelper = new PagerSnapHelper();
    private ArrayList<DaySelectButton> mDaySelectButtons =new ArrayList<>();
    private Context context;
    final LinearLayoutManager mLayoutManager;

    public void setResponder(SomeInterface responder) {
        this.responder = responder;
    }

    private SomeInterface responder;
    //RecycleView
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 10;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        calendar=Calendar.getInstance();
        add60days();

        final DaySelectButtonAdapter adapter = new DaySelectButtonAdapter(mDaySelectButtons,context,this);
        setAdapter(adapter);

        //LayoutManager

        mLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        setLayoutManager(mLayoutManager);
        snapHelper.attachToRecyclerView(this);
        //infiniteScroll(adapter, mLayoutManager);
    }

    private void infiniteScroll(final DaySelectButtonAdapter adapter, final LinearLayoutManager mLayoutManager) {
        addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                //Returns the number of items in the adapter bound to the parent RecyclerView.
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                //Returns the adapter position of the first visible view.
                //This position does not include adapter changes that were dispatched after the last layout pass.
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    /*
                    Log.i("Yaeyec!", "Added 10 days for u boy. adapter size:"+adapter.getItemCount());
                    for(int i=0;i<10;i++)
                    {
                        calendar.add(Calendar.DATE,1);
                        mDaySelectButtons.add(new DaySelectButton(
                                Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)),
                                calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                        ));
                    }
                    adapter.notifyDataSetChanged();*/
                    loading = true;
                }
            }
        });
    }



    private void add60days() {
        //Khoi tao calendar: 60 ngay tu ngay 1 thang truoc
        calendar.add(Calendar.MONTH,-1);

        for(int i=0;i<60;i++)
        {
            mDaySelectButtons.add(new DaySelectButton(
                    Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)),
                    calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()),
                    calendar.getDisplayName(Calendar.MONTH,Calendar.SHORT,Locale.getDefault())));
            calendar.add(Calendar.DATE,1);
        }
    };

    @Override
    public void onDaySelectCLick(int position) {
        //Toast.makeText(context,mDaySelectButtons.get(position).getmDay(),Toast.LENGTH_SHORT).show();
        mLayoutManager.scrollToPositionWithOffset(position,getWidth()/2-mLayoutManager.findViewByPosition(position).getWidth()/2);
        String time=mDaySelectButtons.get(position).getmDay()+"-"+mDaySelectButtons.get(position).getmMonth()+"-2020";
        responder.foo(time);
    }

    public interface SomeInterface
    {
        public void foo(String day);
    }
}
