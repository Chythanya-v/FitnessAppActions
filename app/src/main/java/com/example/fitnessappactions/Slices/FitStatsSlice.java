package com.example.fitnessappactions.Slices;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.slice.Slice;
import androidx.slice.builders.GridRowBuilder;
import androidx.slice.builders.ListBuilder;

import com.example.fitnessappactions.Model.FitActivity;
import com.example.fitnessappactions.Model.FitRepository;
import com.example.fitnessappactions.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class FitStatsSlice extends FitSlice{
    Context context;
    Uri sliceUri;
    FitRepository fitRepo;


    public FitStatsSlice(Uri uri, Context context, Uri sliceUri, FitRepository fitRepo) {
        super(uri, context);
        this.context = context;
        this.sliceUri = sliceUri;
        this.fitRepo = fitRepo;
    }

    /**
     * Get the activity type from the uri and map it to our enum types.
     */
    FitActivity.Type activityType = FitActivity.find(sliceUri.getQueryParameter("exerciseType"));

    /**
     * Create and observe the last activities LiveData.
     */
    LiveData<List<FitActivity>> lastActivities = fitRepo.getLastActivities(5,activityType);



    @Override
    Slice getSlice() {
        List<FitActivity> activity = lastActivities.getValue();
        if (activity != null){
           return createStatsSlice(activity);
        }
        return createLoadingSlice();
    }
    /**
     * Simple loading Slice while the DB is still loading the last activities.
     */
    private Slice createLoadingSlice() {
       return new ListBuilder(context,sliceUri,ListBuilder.INFINITY).addRow(new ListBuilder.RowBuilder()
                .setTitle( context.getString(R.string.slice_stats_loading,activityType.name()))).build();
    }
    /**
     * Create the stats slices showing the data provided by the DB.
     */
    private Slice createStatsSlice(List<FitActivity> activity) {
        String subTitle;
        if (activity.isEmpty())
            subTitle = context.getString(R.string.slice_stats_subtitle_no_data);
            else
                subTitle = context.getString(R.string.slice_stats_subtitle);

        return new ListBuilder(context,sliceUri,ListBuilder.INFINITY).setHeader(new ListBuilder.HeaderBuilder()
               .setTitle(context.getString(R.string.slice_stats_title, activityType.name()))
        .setSubtitle(subTitle))
                .addAction(createActivityAction()).build();

    }

    /**
     * Given a Slice cell, setup the content to display the given FitActivity.
     */
    void cellBuilder(FitActivity activity){
        String distKm =  String.format("%.2f", activity.distanceMeters / 1000);
       String distance = context.getString(R.string.slice_stats_distance, distKm);
        Calendar cal = Calendar.getInstance();
       long date = cal.getTimeInMillis();
       date = activity.date;
        GridRowBuilder.CellBuilder cb = new GridRowBuilder.CellBuilder()
                .addText(distance)
                .addTitleText(cal.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale.getDefault()));
    }

}
