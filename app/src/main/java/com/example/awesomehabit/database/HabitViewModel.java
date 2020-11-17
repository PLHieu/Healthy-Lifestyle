package com.example.awesomehabit.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class HabitViewModel extends AndroidViewModel {
    private HabitRepository habitRepository;
    public final LiveData<List<Habit>> allHabits;
    public HabitViewModel(@NonNull Application application) {
        super(application);
        habitRepository=new HabitRepository(application);
        allHabits=habitRepository.getAllHabits();
    }
    LiveData<List<Habit>> getAllHabits(){return allHabits;}
    public void insert(Habit habit){habitRepository.insert(habit);}
}
