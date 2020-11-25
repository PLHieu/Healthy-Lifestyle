package com.example.awesomehabit.database;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awesomehabit.R;
import com.example.awesomehabit.running.demo;
import com.example.awesomehabit.sleeping.SleepTracker;

import java.util.ArrayList;
import java.util.List;

public class HabitListAdapter extends RecyclerView.Adapter {

    List<Habit> data;
    Context context;
    public void setData(List<Habit> data){
        this.data=data;
        notifyDataSetChanged();
    }

    public HabitListAdapter(Context context) {
       data = new ArrayList<>();
       this.context=context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case Habit.TYPE_RUN:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.running_card,parent,false);
                return new RunHabitViewHolder(view);
            case Habit.TYPE_SLEEP:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.sleeping_card,parent,false);
                return new SleepHabitViewHolder(view);
            case Habit.TYPE_COUNT:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.counting_card,parent,false);
                return new CountingHabitViewHolder(view);
        }
        return null;
    }
    @Override
    public int getItemViewType(int position) {
        switch (data.get(position).type){
            case Habit.TYPE_RUN:
                return Habit.TYPE_RUN;
            case Habit.TYPE_SLEEP:
                return Habit.TYPE_SLEEP;
            case Habit.TYPE_COUNT:
                return Habit.TYPE_COUNT;
        }
        return 0;
        //return super.getItemViewType(position);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        /*Habit current=data.get(position);
        if(current!=null){
            switch (current.type){
                case Habit.TYPE_RUN:
                    ((RunHabitViewHolder)holder).habitName.setText(current.habitName);
                    break;
                case Habit.TYPE_SLEEP:
                    ((SleepHabitViewHolder)holder).sleepHabitName.setText(current.habitName);
                    break;
                    case Habit.TYPE_COUNT:
                        ((CountingHabitViewHolder)holder).countingHabitName.setText(current.habitName);
                        break;
            }
        }*/
    }

    public class RunHabitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView habitName;
        public final Button btn;
        public RunHabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitName=itemView.findViewById(R.id.runHabitName);
            btn=itemView.findViewById(R.id.startRunning);

            btn.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(context,demo.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
    public class SleepHabitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView sleepHabitName;
        private final Button btn;
        public SleepHabitViewHolder(@NonNull View itemView) {
            super(itemView);
            sleepHabitName =itemView.findViewById(R.id.sleepHabitName);
            btn=itemView.findViewById(R.id.startSleeping);

            btn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent=new Intent(context, SleepTracker.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
    public class CountingHabitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView countingHabitName;
        private final Button btnMinus;
        private final Button btnAdd;
        public CountingHabitViewHolder(@NonNull View itemView) {
            super(itemView);
            countingHabitName=itemView.findViewById(R.id.countingHabitName);
            btnAdd=itemView.findViewById(R.id.add);
            btnMinus=itemView.findViewById(R.id.minus);
            btnAdd.setOnClickListener(this);
            btnMinus.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {

        }
    }
}
