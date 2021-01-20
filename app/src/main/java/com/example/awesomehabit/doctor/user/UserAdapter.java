package com.example.awesomehabit.doctor.user;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awesomehabit.R;
import com.example.awesomehabit.database.user.User;
import com.example.awesomehabit.doctor.MainActivityDoctor;
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

    Context _context;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        _context = parent.getContext();
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(_context, MainActivityDoctor.class);
                    _context.startActivity(i);
                }
            });
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
