package com.example.remindme.job;

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.BaseColumns;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.remindme.TaskDataModel;
import com.example.remindme.db.TaskContract;
import com.example.remindme.db.TaskDbHelper;
import com.example.remindme.services.HttpUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PlaceFinderJob extends JobService {

    private TaskDbHelper mDbHelper;
    private SQLiteDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        mDbHelper = new TaskDbHelper(this);
        db = mDbHelper.getReadableDatabase();
        Log.d("Test", "***** Job Created *****");
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("Job", "*************JOB Running*************************");

        List<String> categories = getCategories();
        Log.d("Categories", "*****" + categories.size());


        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("Permission", "****No Permission avaialble");
            return true;
        }
        Location loc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ loc.getLatitude() +"," + loc.getLongitude() + "&radius=1500&key=AIzaSyAzzvLxqgKxiohWzYFPYl1gXzJY_-6sgZc&type=";

        RequestParams rp = new RequestParams();

        for(String category: categories) {
            HttpUtils.get(url+category, rp, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String server_response = null;
                    try {
                        server_response = String.valueOf(new String(responseBody, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.v("Server response",server_response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    String server_response = null;
                    try {
                        server_response = String.valueOf(new String(responseBody, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.v("Server Error response",server_response);
                    Log.v("Server error",error.toString());

                }
            });
        }


        Util.scheduleJob(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }


    private List<String> getCategories() {
        String[] projection = {
                BaseColumns._ID,
                TaskContract.TaskEntry.COL_TASK_CATEGORY
        };

        String selection = TaskContract.TaskEntry.COL_TASK_STATUS + " = ?";
        String[] selectionArgs = { "OPEN" };


        Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        List<String> categories = new ArrayList<>();

        while(cursor.moveToNext()) {
            String taskCategory = cursor.getString(
                    cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COL_TASK_CATEGORY));

            categories.add(taskCategory);
        }
        cursor.close();

        return categories;
    }
}
