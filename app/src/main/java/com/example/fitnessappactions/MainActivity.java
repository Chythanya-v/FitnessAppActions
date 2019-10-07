package com.example.fitnessappactions;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.fitnessappactions.tracking.FitTrackingService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        if(action != null){
            switch (action){
                case Intent.ACTION_VIEW:
                    handleDeepLink(data);

                case Intent.ACTION_SEARCH:
                    handleDeepLink(Uri.parse(intent.getStringExtra(SearchManager.QUERY)));
            }
        }
    }

    private void handleDeepLink(Uri data) {
        // path is normally used to indicate which view should be displayed
        // i.e https://fit-actions.firebaseapp.com/start?exerciseType="Running" -> "start" will be the path
        boolean actionHandled = true;
String path = data.getPath();

switch(path){
    case "/stop" :
stopService(new Intent(this,FitTrackingService.class));

break;

    case "/start" :
String exerciseType = data.getQueryParameter(DeepLink.P)
}
    }
}
