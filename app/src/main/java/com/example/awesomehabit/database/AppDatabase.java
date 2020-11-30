package com.example.awesomehabit.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.awesomehabit.database.running.Run;
import com.example.awesomehabit.database.running.RunDao;
import com.example.awesomehabit.database.sleeping.SleepDatabaseDao;
import com.example.awesomehabit.database.sleeping.SleepNight;
import com.example.awesomehabit.database.water.Water;
import com.example.awesomehabit.database.water.WaterDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static androidx.room.RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING;

@Database(entities = {Habit.class, Run.class, SleepNight.class, HabitGoal.class, Water.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    //public abstract HabitDao habitDao();
    public abstract RunDao runDao();
    public abstract SleepDatabaseDao sleepDao();
    public abstract HabitGoalDao habitGoalDao();
    public abstract WaterDao waterDao();
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "habit_database").addCallback(sRoomDatabaseCallback)
                            .allowMainThreadQueries().setJournalMode(WRITE_AHEAD_LOGGING).build();
                }
            }
        }
        return INSTANCE;
    }

    static RoomDatabase.Callback sRoomDatabaseCallback=new RoomDatabase.Callback(){
        /**
         * Called when the database is created for the first time. This is called after all the
         * tables are created.
         *
         * @param db The database.
         */
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
/*                    HabitDao dao=INSTANCE.habitDao();
                    Habit habit=new Habit(1,"Running",Habit.TYPE_RUN);
                    dao.insert(habit);
                    habit=new Habit(2,"Sleeping",Habit.TYPE_SLEEP);
                    dao.insert(habit);
                    habit=new Habit(3,"Water drank",Habit.TYPE_COUNT);
                    dao.insert(habit);*/

                    // init dump data
                    // in tracking running for 1 month from 1/11 - 30/11/2020
                    Random rand = new Random();
                    for (int i = 1; i <= 30; i++ ){

                        int hour = rand.nextInt(18);
                        int minute = rand.nextInt(50);
                        int second = rand.nextInt(50);

                        Calendar calendar = new GregorianCalendar(2020,10,i,hour,minute,second);

                        Date date = calendar.getTime();
                        String timestamp = String.valueOf(date.getTime());
                        Run run  = new Run(rand.nextInt(5000)
                                ,timestamp, calendar, rand.nextInt(14000), "");

                        INSTANCE.runDao().insertRun(run);
                    }

                }
            });
        }
    };
}
