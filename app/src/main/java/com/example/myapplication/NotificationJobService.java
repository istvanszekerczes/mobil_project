package com.example.myapplication;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class NotificationJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        new NotificationHandler(getApplicationContext()).send("Time to create a new Tournament!");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
