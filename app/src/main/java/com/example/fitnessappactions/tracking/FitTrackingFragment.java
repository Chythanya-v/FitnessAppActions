package com.example.fitnessappactions.tracking;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitnessappactions.MainActivity;
import com.example.fitnessappactions.Model.FitActivity;
import com.example.fitnessappactions.Model.FitRepository;
import com.example.fitnessappactions.R;

/**
 * Fragment that handles the starting of an activity and tracks the status.
 *
 * When the fragments starts, it will start a countdown and launch the foreground service
 * that will keep track of the status.
 *
 * The view will observe the status and update its content.
 */
public class FitTrackingFragment extends Fragment {
    Button startActivityButton;
    Context mcontext;
    String PARAM_TYPE = "type";
   // FitTrackingActions actionCallBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mcontext = getContext();
        return inflater.inflate(R.layout.fit_tracking_fragment,container,false);

    }

    FitRepository fitRepository = FitRepository.getInstance(mcontext);
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FitActivity fitActivity = fitRepository.startActivity();
            }
        });
    }
}
