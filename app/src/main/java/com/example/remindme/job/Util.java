package com.example.remindme.job;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;

public class Util {

    // schedule the start of the service every 10 - 30 seconds
    public static void scheduleJob(Context context) {
        PersistableBundle bundle = new PersistableBundle();
        ComponentName serviceComponent = new ComponentName(context, PlaceFinderJob.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1 * 5000); // wait at least
        builder.setOverrideDeadline(3 * 1000); // maximum delay
        builder.setExtras(bundle);
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

}