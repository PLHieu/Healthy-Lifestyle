package com.example.awesomehabit;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DaySelectButtonAdapter extends RecyclerView.Adapter<DaySelectButtonAdapter.ViewHolder>{
    private static final String TAG ="RecyclerViewAdapter";
    //private ArrayList<DaySelectButton> mDaySelectButtons;
    private Context mContext;
    private OnDaySelectListener onDaySelectListener;

    public void setCheckedPosition(int checkedPosition) {
        //this.checkedPosition = checkedPosition;
        int oldCheckedPos=this.checkedPosition;
        this.checkedPosition=checkedPosition;

        notifyItemChanged(oldCheckedPos);
        notifyItemChanged(this.checkedPosition);
    }

    private int checkedPosition =0;

    private Calendar calendar;
    public DaySelectButtonAdapter(Context mContex,OnDaySelectListener onDaySelectListener) {
        //this.mDaySelectButtons=daySelectButtons;
        this.mContext = mContext;
        this.onDaySelectListener=onDaySelectListener;
        calendar=Calendar.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item_layout,parent,false);
        ViewHolder holder = new ViewHolder(view,onDaySelectListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Calendar c= (Calendar) calendar.clone();
        c.add(Calendar.DATE,position-CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2);
        holder.tvMonth.setText(c.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault()));
        holder.tvDay.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
        holder.tvDayOfWeek.setText(c.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.SHORT,Locale.getDefault()));
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return CustomCalendarView.NUMBER_OF_DAY_BUTTONS;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvDay;
        TextView tvDayOfWeek;
        TextView tvMonth;
        LinearLayout parentLayout;

        OnDaySelectListener onDaySelectListener;
        public ViewHolder(@NonNull View itemView,OnDaySelectListener onDaySelectListener) {
            super(itemView);

            tvDay=itemView.findViewById(R.id.calendar_day);
            tvDayOfWeek=itemView.findViewById(R.id.calendar_dayOfWeek);
            tvMonth=itemView.findViewById(R.id.calendar_month);
            parentLayout=itemView.findViewById(R.id.calendar_parent);
            this.onDaySelectListener=onDaySelectListener;
            itemView.setOnClickListener(this);
        }
        void bind()
        {
            if(checkedPosition ==-1)
            {
                tvMonth.setTextColor(Color.WHITE);
                tvDay.setTextColor(Color.WHITE);
                tvDayOfWeek.setTextColor(Color.WHITE);
            }
            else {
                if (checkedPosition == getAdapterPosition()) {
                    tvMonth.setTextColor(Color.YELLOW);
                    tvDay.setTextColor(Color.YELLOW);
                    tvDayOfWeek.setTextColor(Color.YELLOW);
                } else {
                    tvMonth.setTextColor(Color.WHITE);
                    tvDay.setTextColor(Color.WHITE);
                    tvDayOfWeek.setTextColor(Color.WHITE);
                }
            }
        }
        /* Called when a view has been clicked.
         * @param v The view that was clicked*/
        @Override
        public void onClick(View v) {
            tvMonth.setTextColor(Color.YELLOW);
            tvDay.setTextColor(Color.YELLOW);
            tvDayOfWeek.setTextColor(Color.YELLOW);
            if(checkedPosition !=getAdapterPosition())
            {
                notifyItemChanged(checkedPosition);
                checkedPosition =getAdapterPosition();
            }
            onDaySelectListener.onDaySelectCLick(getAdapterPosition());
        }
    }

    public interface OnDaySelectListener{
        void onDaySelectCLick(int position);
    }
}
