package com.example.awesomehabit.meal;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.awesomehabit.R;

import java.util.ArrayList;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {

    ArrayList<Meal> meals;

    public MealAdapter(ArrayList<Meal> meals) {
        this.meals = meals;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView foodName;
        TextView foodCalo;
        ImageView foodImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foodName=itemView.findViewById(R.id.foodItem_name);
            foodCalo=itemView.findViewById(R.id.foodItem_calo);
            foodImage=itemView.findViewById(R.id.foodItem_image);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.foodName.setText(meals.get(position).getName());
        holder.foodCalo.setText(String.valueOf(meals.get(position).getCalories()));
    }

    @Override
    public int getItemCount() { return meals.size();

    }
}
