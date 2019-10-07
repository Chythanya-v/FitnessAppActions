package com.example.fitnessappactions.Home;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessappactions.R;

import java.util.Calendar;

public class FitStatsAdapter {


    class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

          Calendar  calendar = Calendar.getInstance().apply { timeInMillis = activity.date }

            itemView.statsRowTitle = context.getString(
                    R.string.stat_date,
                    day,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH)
            )
        }
    }
}
