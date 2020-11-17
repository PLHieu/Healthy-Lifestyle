package com.example.awesomehabit.database.running;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Run.class}, version = 1)
public abstract class RunDatabase extends RoomDatabase {

    public abstract RunDao runDao();

    private static volatile RunDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static RunDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RunDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RunDatabase.class, "user_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
