package com.example.awesomehabit.doctor.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awesomehabit.R;
import com.example.awesomehabit.database.user.User;
import com.example.awesomehabit.doctor.Ults;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    List<User> users;

    public void setUsers(List<User> users){
        this.users=users;
        notifyDataSetChanged();
    }
    public UserAdapter(ArrayList<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvAddress;
        ImageView ivAvatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tvUserName);
            tvAddress=itemView.findViewById(R.id.tvUserAddress);
            ivAvatar=itemView.findViewById(R.id.imageViewAvatar);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(users.get(position).name);
        holder.tvAddress.setText(users.get(position).address);
        if(users.get(position).avatarBitmap!=null && users.get(position).avatarBitmap.length()>20)
            holder.ivAvatar.setImageBitmap(Ults.getBitmapFromString(users.get(position).avatarBitmap) );

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


}