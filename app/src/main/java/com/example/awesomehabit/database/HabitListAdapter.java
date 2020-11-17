package com.example.awesomehabit.database;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awesomehabit.R;
import com.example.awesomehabit.running.demo;
import com.example.awesomehabit.sleeping.SleepTracker;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class HabitListAdapter extends ListAdapter<Habit, HabitListAdapter.HabitViewHolder> {

    public HabitListAdapter(@NonNull DiffUtil.ItemCallback<Habit> diffCallback) {
        super(diffCallback);

    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.running_habit, parent, false);
        Button btn=view.findViewById(R.id.startRunning);
        Button btn2=view.findViewById(R.id.startTiming);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent=new Intent(getApplicationContext(), demo.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//                Mapbox.getInstance(getApplicationContext(), getApplicationContext().getString(R.string.mapbox_access_token));
                getApplicationContext().startActivity(intent);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent=new Intent(getApplicationContext(), SleepTracker.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//                Mapbox.getInstance(getApplicationContext(), getApplicationContext().getString(R.string.mapbox_access_token));
                getApplicationContext().startActivity(intent);
            }
        });
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit current=getItem(position);
        holder.bind(current.habitName);
    }

    public class HabitViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView habitItemView;
        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitItemView=itemView.findViewById(R.id.habitName);
        }
        public void bind(String text){
            habitItemView.setText(text);
        }
    }

    public static class HabitDiff extends DiffUtil.ItemCallback<Habit> {

        @Override
        public boolean areItemsTheSame(@NonNull Habit oldItem, @NonNull Habit newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Habit oldItem, @NonNull Habit newItem) {
            return oldItem.habitName.equals(newItem.habitName);
        }
    }
}
