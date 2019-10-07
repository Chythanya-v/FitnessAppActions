package com.example.fitnessappactions.Slices;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.ListBuilder.RowBuilder;
import androidx.slice.builders.SliceAction;

import com.example.fitnessappactions.Model.FitRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FitSliceProvider extends SliceProvider {

    /**
     * Keep track of the created slices so when the slice calls "refresh" we don't create it again.
     */
    Map lastSlices = new HashMap<Uri,FitSlice>();

   private FitSlice createNewSlice(Uri sliceUri){

       FitRepository fitRepository = FitRepository.getInstance(getContext());

       if (sliceUri.getPath() != null){
         return new FitStatsSlice(sliceUri,getContext(),sliceUri,fitRepository );
       }
       else
           return new FitSlice.fitSliceDefault(sliceUri,getContext());
   }
    /**
     * Instantiate any required objects. Return true if the provider was successfully created,
     * false otherwise.
     */
    @Override
    public boolean onCreateSliceProvider() {
        return true;
    }

    /**
     * Converts URL to content URI (i.e. content://com.example.fitnessappactions.Slices...)
     */
    @Override
    @NonNull
    public Uri onMapIntentToUri(@Nullable Intent intent) {
        // Note: implementing this is only required if you plan on catching URL requests.
        // This is an example solution.
        Uri.Builder uriBuilder = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT);
        if (intent == null) return uriBuilder.build();
        Uri data = intent.getData();
        if (data != null && data.getPath() != null) {
            String path = data.getPath().replace("/", "");
            uriBuilder = uriBuilder.path(path);
        }
        Context context = getContext();
        if (context != null) {
            uriBuilder = uriBuilder.authority(context.getPackageName());
        }
        return uriBuilder.build();
    }

    /**
     * Construct the Slice and bind data if available.
     */
    public Slice onBindSlice(Uri sliceUri) {
        Context context = getContext();
        Object o;
        SliceAction activityAction = createActivityAction();
// When a new request is send to the SliceProvider of this app, this method is called
        // with the given slice URI.
        // Here you could directly handle the uri and create a new slice. But in order to make
        // the slice dynamic and better structured, we use the FitSlice class.
        // Then we check if the new slice uri is the same as the last created slices (if any).
        // If there was none, we create a new instance of FitSlice and return the Slice instance.
       if(lastSlices.get(sliceUri) == null){
         return  createNewSlice(sliceUri).getSlice();
       }
       else
           return (Slice)lastSlices.get(sliceUri);

        /*
        if (context == null || activityAction == null) {
            return null;
        }
        if ("/".equals(sliceUri.getPath())) {
            // Path recognized. Customize the Slice using the androidx.slice.builders API.
            // Note: ANRs and strict mode is enforced here so don't do any heavy operations.
            // Only bind data that is currently available in memory.
            return new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                    .addRow(
                            new RowBuilder()
                                    .setTitle("URI found.")
                                    .setPrimaryAction(activityAction)
                    )
                    .build();
        } else {
            // Error: Path not found.
            return new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                    .addRow(
                            new RowBuilder()
                                    .setTitle("URI not found.")
                                    .setPrimaryAction(activityAction)
                    )
                    .build();
        }
        */
    }

    private SliceAction createActivityAction() {
        return null;
        //Instead of returning null, you should create a SliceAction. Here is an example:
        /*
        return SliceAction.create(
            PendingIntent.getActivity(
                getContext(), 0, new Intent(getContext(), MyActivityClass.class), 0
            ),
            IconCompat.createWithResource(getContext(), R.drawable.ic_launcher_foreground),
            ListBuilder.ICON_IMAGE,
            "Open App"
        );
        */
    }

    /**
     * Slice has been pinned to external process. Subscribe to data source if necessary.
     */
    @Override
    public void onSlicePinned(Uri sliceUri) {
        // When data is received, call context.contentResolver.notifyChange(sliceUri, null) to
        // trigger FitSliceProvider#onBindSlice(Uri) again.
    }

    /**
     * Unsubscribe from data source if necessary.
     */
    @Override
    public void onSliceUnpinned(Uri sliceUri) {
        // Remove any observers if necessary to avoid memory leaks.
    }
}
