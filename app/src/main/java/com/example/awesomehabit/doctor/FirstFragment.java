package com.example.awesomehabit.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.awesomehabit.R;
import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.user.User;
import com.example.awesomehabit.doctor.user.UserAdapter;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    ArrayList<User> users=new ArrayList<>();
    UserAdapter userAdapter=new UserAdapter(users);

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv=view.findViewById(R.id.rvUserList);

        AppDatabase db=AppDatabase.getDatabase(this.getContext());
        db.userDao().getAllPerson().observe(this.getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> people) {

                userAdapter.setUsers(people);
            }
        });

        rv.setAdapter(userAdapter);

        rv.setLayoutManager(new LinearLayoutManager(this.getContext()));

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //NavHostFragment.findNavController(FirstFragment.this)
                 //       .navigate(R.id.action_loginFragment_to_registerFragment);
//                Intent i = new Intent(getContext(),MainActivityDoctor.class);
//                startActivity(i);
            }
        });
    }
}