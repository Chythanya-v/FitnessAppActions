package com.example.fitnessappactions.Model;

import android.content.Context;
import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Simple repository to retrieve fit activities and start/stop new ones.
 *
 * It uses a Local DB to store/read FitActivities.
 */
public class FitRepository {

    private Handler handler = new Handler();
    FitDatabase fitdb;
    Executor ioExecutor;

    private static volatile FitRepository INSTANCE;

    //constructor for the fitRepository
    public FitRepository(FitDatabase fitdb, Executor ioExecutor) {
        this.fitdb = fitdb;
        this.ioExecutor = ioExecutor;
    }

    //get instance is a static method which allows other classes to get handle of repository class.
    public static FitRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (FitRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FitRepository(FitDatabase.getInstance(context),
                            Executors.newSingleThreadExecutor());
                }
                return INSTANCE;
            }
        }
        return INSTANCE;
    }


    FitActivity fitActivity = new FitActivity(String.valueOf(Math.random()), System.currentTimeMillis(),
            FitActivity.Type.RUNNING, 0.0, 0);

    /**
     * LiveData containing the active tracker or null if none.
     */
    private MutableLiveData<Tracker> currentTracker() {
        return new MutableLiveData<>();
    }

    /**
     * Keep the transformation as variable to avoid creating a new one
     * each time getOnGoingActivity is called.
     */
    private LiveData<FitActivity> _onGoingActivity() {
        MutableLiveData<FitActivity> Activity = new MutableLiveData<FitActivity>();
        Transformations.switchMap(Activity, currentTracker());
    }

    /**
     * Get the last activities
     *
     * @param count maximum number of activities to return
     * @return a live data with the last FitActivity
     */
    LiveData<List<FitActivity>> getLastActivities(int count, FitActivity.Type type) {
        FitActivityDao dao = fitdb.fitActivityDao();
        if (type != null) {
            return dao.getAllOfType(type, count);
        } else
            return dao.getAll(count);

    }

    /**
     * Get the current users stats
     *
     * @return a live data with the latest FitStats
     */
    LiveData<FitStats> getStats() {
        return fitdb.fitActivityDao().getStats();
    }

    /**
     * Get the on going activity
     *
     * @return a live data that tracks the ongoing activity if any.
     */
    LiveData<FitActivity> getOnGoingActivity() {
        return _onGoingActivity();
    }

    /**
     * Start a new activity.
     * <p>
     * This method will stop any previous activity and create a new one.
     *
     * @see getOnGoingActivity
     * @see stopActivity
     */
    startActivity() {
        stopActivity();
        currentTracker().v = new Tracker();
    }

    /**
     * Stop the ongoing activity if any and store the result.
     * <p>
     * Note: the storing will be performed async.
     */
    stopActivity() {
        currentTracker.value ?.let {
            tracker ->
                    currentTracker.value = null;
            ioExecutor.execute {
                tracker.stop()
                fitdb.fitActivityDao().insert(tracker.value)
            }
        }
    }


    /**
     * Internal class to track a FitActivity.
     * <p>
     * It will automatically start tracking the duration and distance on creation, updating the
     * stats and notifying observers.
     */
    public class Tracker extends MutableLiveData<FitActivity> {

        private boolean isrunning = true;
        public Runnable runnable = new Runnable() {

            /**
             * Method that will run every second while isRunning is true,
             * updating the FitActivity and notifying the observers of the LiveData.
             */
            @Override
            public void run() {
                fitActivity.durationMs = System.currentTimeMillis() - fitActivity.date;
                fitActivity.distanceMeters = fitActivity.distanceMeters + 10;

                if (isrunning) {
                    handler.postDelayed(this, 1000);
                }
            }
        };

        //gets the fitActivity object from the MutableLiveData class which is the super class for the tracker class.
        public FitActivity getValue() {
            if (super.getValue() != null) {
                return super.getValue();
            } else
                return null;

        }

        /**
         * Stop tracking activity.
         */
        void stop() {
            isrunning = false;
            handler.removeCallbacks(runnable);
        }
    }
}
