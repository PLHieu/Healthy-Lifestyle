package com.example.awesomehabit.statistic;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyWeekDayRecyclerViewAdapter extends RecyclerView.Adapter<MyWeekDayRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "MyWeekDayRecyclerViewAdapter";

    private ArrayList<String> listTxtViewWeekDay = new ArrayList<>();
    private ArrayList<View.OnClickListener> listTxtViewOnClick = new ArrayList<>();

    public MyWeekDayRecyclerViewAdapter(ArrayList<String> listTxtViewWeekDay) {
        this.listTxtViewWeekDay = listTxtViewWeekDay;
        listTxtViewOnClick = null;
    }

    public MyWeekDayRecyclerViewAdapter(ArrayList<String> listTxtViewWeekDay, ArrayList<View.OnClickListener> listTxtViewOnClick) {
        this.listTxtViewWeekDay = listTxtViewWeekDay;
        this.listTxtViewOnClick = listTxtViewOnClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_day, parent, false);
        ViewHolder holder = new ViewHolder(new View(parent.getContext()));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtViewLabel.setText(listTxtViewWeekDay.get(position));

        if (listTxtViewOnClick != null && listTxtViewOnClick.get(position) != null)
            holder.txtViewLabel.setOnClickListener(listTxtViewOnClick.get(position));
    }

    @Override
    public int getItemCount() {
        return listTxtViewWeekDay.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtViewLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //txtViewLabel = (TextView)itemView.findViewById(R.id.textViewWeekday);
        }
    }
}
