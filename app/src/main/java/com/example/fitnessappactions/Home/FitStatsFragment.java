package com.example.fitnessappactions.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.fitnessappactions.Model.FitActivity;
import com.example.fitnessappactions.Model.FitRepository;
import com.example.fitnessappactions.Model.FitStats;
import com.example.fitnessappactions.R;

import java.util.concurrent.TimeUnit;

public class FitStatsFragment extends Fragment {

     FitStatsActions actionsCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fit_stats_fragment, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FitStatsAdapter adapter = new FitStatsAdapter();
        statsList.adapter = adapter;

        FitRepository repository = FitRepository.getInstance(getContext());
        repository.getStats.Observe(getViewLifecycleOwner(),
                new Observer<FitStats>() {
                    @Override
                    public void onChanged(FitStats fitStats) {
                       String statsActivityCount = getString(
                                R.string.stats_total_count,
                                fitStats.TotalCount
                        );
                      String  statsDistanceCount = getString(
                                R.string.stats_total_distance,
                                fitStats.totalDistanceMeters
                        );

                       long  durationInMin = TimeUnit.MILLISECONDS.toMinutes(fitStats.totalDurationMs);
                       String statsDurationCount = getString(R.string.stats_total_duration, durationInMin);
                    }
                });

        repository.getLastActivities(10).observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                adapter.submitList();
                statsList.smoothScrollToPosition(0);
            }
        });


        }
    interface FitStatsActions {
        void onStartActivity();
    }
    }




