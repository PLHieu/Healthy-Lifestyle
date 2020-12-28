package com.example.awesomehabit.meal;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.awesomehabit.R;
import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.generated.callback.OnClickListener;

import java.util.ArrayList;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {
    ArrayList<Meal> meals;
    private OnMealListener mOnMealListener;
    public void setMeals(ArrayList<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }



    public MealAdapter(ArrayList<Meal> meals,OnMealListener onMealListener) {
        this.meals = meals;
        this.mOnMealListener=onMealListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView foodName;
        TextView foodCalo;
        ImageView foodImage;
        ImageButton removeButton;
        OnMealListener onMealListener;
        public ViewHolder(@NonNull View itemView,OnMealListener onMealListener) {
            super(itemView);
            foodName=itemView.findViewById(R.id.foodItem_name);
            foodCalo=itemView.findViewById(R.id.foodItem_calo);
            foodImage=itemView.findViewById(R.id.foodItem_image);
            this.onMealListener=onMealListener;
            removeButton=itemView.findViewById(R.id.btn_removeMeal);
            removeButton.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            onMealListener.onMealClick(getAdapterPosition());
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);
        ViewHolder holder = new ViewHolder(view,mOnMealListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.foodName.setText(meals.get(position).getName());
        holder.foodCalo.setText(String.valueOf(meals.get(position).getCalories())+" calories");
        if(meals.get(position).getBitmap()!=null)
        holder.foodImage.setImageBitmap(meals.get(position).getBitmap());
    }

    @Override
    public int getItemCount() { return meals.size(); }

    public interface OnMealListener{
        void onMealClick(int position);
    }
}
