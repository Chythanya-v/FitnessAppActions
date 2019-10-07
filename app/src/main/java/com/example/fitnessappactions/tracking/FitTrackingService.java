package com.example.fitnessappactions.tracking;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;

import com.example.fitnessappactions.MainActivity;
import com.example.fitnessappactions.Model.FitActivity;
import com.example.fitnessappactions.Model.FitRepository;
import com.example.fitnessappactions.R;


public class FitTrackingService extends Service {

    /*
    Create an instance for the fitRepository
     */
    private FitRepository getFitRepository()  {
       return FitRepository.getInstance(this);
    }

    /**
     * Create a notification builder that will be used to create and update the stats notification
     */
    private NotificationCompat.Builder  notificationBuilder()  {

        PendingIntent p = PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),0);
       return new NotificationCompat.Builder(this, "TrackingChannel")
                .setContentTitle(getText(R.string.tracking_notification_title))
                .setSmallIcon(R.drawable.ic_run)
                .setContentIntent(p)
                .setPriority(NotificationCompat.PRIORITY_MAX);
    }

    /**
     * Observer that will update the notification with the ongoing activity status.
     */

    private Observer<FitActivity> trackingObserver(){
        Observer<FitActivity> observer = new Observer<FitActivity>() {
            @Override
            public void onChanged(FitActivity fitActivity) {
                String km = String.format("%.2f", fitActivity.distanceMeters / 1000);
                Notification notification = notificationBuilder()
                        .setContentText(getString(R.string.stat_distance, km))
                        .build();
            }

        };
        return observer;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(999, notificationBuilder().build());

        // Start a new activity and attach the observer
        getFitRepository().startActivity();
        getFitRepository().getOngoingActivity().observeForever(trackingObserver();
//        getFitRepository().getOnGoingActivity().observeForever(trackingObserver)
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the ongoing activity and detach the observer
        getFitRepository().stopActivity();
        getFitRepository().getOngoingActivity().removeObserver(trackingObserver());
    }

    /**
     * Creates a Notification channel needed for new version of Android
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("TrackingChannel", name, importance);
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(channel);
        }
    }
}
