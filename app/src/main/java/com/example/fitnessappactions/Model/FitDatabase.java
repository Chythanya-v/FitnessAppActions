package com.example.fitnessappactions.Model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.sqlite.db.SupportSQLiteDatabase;


import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Database(entities = {FitActivity.class}, version = 1)
public abstract class FitDatabase extends RoomDatabase {
    public abstract FitActivityDao fitActivityDao();

    private static volatile FitDatabase INSTANCE;

    public synchronized static FitDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = getDatabase(context);
        }
        return INSTANCE;
    }

    public static FitDatabase getDatabase(final Context context){
        if (INSTANCE == null) {
            synchronized (FitDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FitDatabase.class, "fitness_database")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    prepopulate(context);
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    /**
     * Prepopulate the database with some mock activities, normally this would not be needed.
     */
    private static void prepopulate(final Context context){
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                FitActivityDao fitActivityDao = getInstance(context).fitActivityDao();
                long currentTime = System.currentTimeMillis();
                int i =0;
                while(i<=10) {
                    fitActivityDao.insert(new FitActivity(
                            String.valueOf(Math.random()),
                            currentTime,
                            FitActivity.Type.RUNNING,
                            (250.00) * 1000,
                            currentTime
                    ));
                    i++;
                }
            }
        });
    }

    /**
     * Converter class for the DB to convert from/to enum FitActivity.Type
     */
    class Converters {

        @TypeConverter
        //takes the enum type and converts it into integer.
        //values.ordinal() returns the ordinal number of the enum value which is an integer
       int fromType(FitActivity.Type value) {
            return value.ordinal();
        }

        @TypeConverter
        FitActivity.Type toType(int value){
            //get the array of the activities
                     FitActivity.Type[] activities =   FitActivity.Type.values();
          //check if the integer passed is less than the size of array, if it less then pass the enum value o that integer
         if (value < activities.length)
             return activities[value];
         //else pass unknown
         else
             return FitActivity.Type.UNKNOWN;

        }
    }
}
