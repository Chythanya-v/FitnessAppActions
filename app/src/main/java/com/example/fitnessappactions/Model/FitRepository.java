package com.example.fitnessappactions.Model;

import android.content.Context;
import android.os.Handler;

import androidx.arch.core.util.Function;
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
    private volatile static FitRepository INSTANCE;
    private Handler handler = new Handler();
    FitDatabase fitdb ;
    Executor ioExecutor;

    public synchronized static FitRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new FitRepository(
                    FitDatabase.getInstance(context), Executors.newSingleThreadExecutor());

        }
        return INSTANCE;
    }


    /**
     * LiveData containing the active tracker or null if none.
     */
    private MutableLiveData<Tracker> currentTracker(){

        MutableLiveData<Tracker> tracker = new MutableLiveData<>();
        if(tracker.getValue() != null){
            return tracker;
        }else
            return null;

    }

    FitActivity fitActivity = new FitActivity(String.valueOf(Math.random()), System.currentTimeMillis(),
            FitActivity.Type.RUNNING, 0.0, 0);
    /**
     * Keep the transformation as variable to avoid creating a new one
     * each time getOnGoingActivity is called.
     */
   private LiveData<FitActivity> _onGoingActivity() {
       MutableLiveData<FitActivity> liveActivity = new MutableLiveData<>();
      return Transformations.switchMap(currentTracker());


   }
    /**
     * Start a new activity.
     *
     * This method will stop any previous activity and create a new one.
     *
     * @see getOnGoingActivity
     * @see stopActivity
     */
   public void startActivity() {
        stopActivity();
        Tracker tracker = new Tracker();
        currentTracker().postValue(tracker);
    }

    /**
     * Stop the ongoing activity if any and store the result.
     *
     * Note: the storing will be performed async.
     */
   public void stopActivity() {
        currentTracker().value?.let { tracker ->
                currentTracker()..value = null
            ioExecutor.execute {
                tracker.stop()
                fitDb.fitActivityDao().insert(tracker.value)
            }
        }


    //get user stats
    LiveData<FitStats> getStats() {
        return  fitdb.fitActivityDao().getStats();
    }

    //get ongoing activity
   public LiveData<FitActivity> getOngoingActivity() {
        return _onGoingActivity();
    }

    /**
     * Get the last activities
     *
     * @param count maximum number of activities to return
     * @return a live data with the last FitActivity
     */
    public LiveData<List<FitActivity>> getLastActivities(int count, FitActivity. type) {
        FitActivityDao dao = fitdb.fitActivityDao();
        if (type != null)
            return dao.getAll(count);
        else
            return dao.getAllOfType(type, count);
    }
    /**
     * Internal class to track a FitActivity.
     *
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
//gets the fitActivity object from the Live data.
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
