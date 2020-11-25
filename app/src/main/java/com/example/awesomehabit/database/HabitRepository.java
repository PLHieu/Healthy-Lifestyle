package com.example.awesomehabit.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class HabitRepository {
    private HabitDao mHabitDao;
    private LiveData<List<Habit>> mAllHabits;

    HabitRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        //mHabitDao = db.habitDao();
        //mAllHabits = mHabitDao.getAll();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Habit>> getAllHabits() {
        return mAllHabits;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    /*void insert(final Habit habit) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mHabitDao.insert(habit);
            }
        });
    }*/
}
