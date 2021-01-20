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
    UserInterface userInterface;
    public void setUsers(List<User> users){
        this.users=users;
        notifyDataSetChanged();
    }
    public UserAdapter(List<User> users,UserInterface userInterface) {
        this.userInterface=userInterface;
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            userInterface.onUserClick(getAdapterPosition());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(users.get(position).name);
        holder.tvAddress.setText(users.get(position).diachi);
        if(users.get(position).profile_pic!=null && users.get(position).profile_pic.length()>20)
            holder.ivAvatar.setImageBitmap(Ults.getBitmapFromString(users.get(position).profile_pic) );

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public interface UserInterface{
        void onUserClick(int position);
    }

}
