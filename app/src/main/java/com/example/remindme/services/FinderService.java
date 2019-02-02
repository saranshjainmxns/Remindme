package com.example.remindme.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.remindme.PlaceDataModel;
import com.example.remindme.R;
import com.example.remindme.db.PlaceContract;
import com.example.remindme.db.PlaceDbHelper;
import com.example.remindme.db.TaskContract;
import com.example.remindme.db.TaskDbHelper;
import com.example.remindme.mapper.PlaceMapper;
import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cz.msebera.android.httpclient.Header;

public class FinderService extends IntentService implements LocationListener {

    public static final String API_KEY = "AIzaSyAzzvLxqgKxiohWzYFPYl1gXzJY_-6sgZc";
    private LocationManager mLocationManager;
    private TaskDbHelper mDbHelper;
    private PlaceDbHelper placeDbHelper;
    private SQLiteDatabase db;
    private SQLiteDatabase placeDb;
    private GeoApiContext context;


    public FinderService() {
        super("Finder Service");
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        Log.d("Finder Service", "**** Created ******");
        super.onCreate();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mDbHelper = new TaskDbHelper(this);
        placeDbHelper = new PlaceDbHelper(this);

        context = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 100, this);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    private boolean requestInProgress = true;
    @Override
    public void onLocationChanged(Location loc) {
        Log.d("Location changed","***** Latitude: "+ loc.getLatitude() + "  \n Longitude: " + loc.getLongitude());

        requestInProgress = true;

        List<String> categories = getCategories();
        Log.d("Categories", "*****" + categories.size());

//        truncatePlaceTable();

        try {
            for(String category: categories) {
                Log.d("Fetching places Response for category", "************" + category);
                List<PlaceDataModel> places = getPlaceesForCategory(loc, category);


                places.forEach(e -> {
                    createPlace(e);
                });
            }
        } catch (Exception e) {
            Log.d("Error:::::::::::", e.toString());
        } finally {
            requestInProgress = false;
        }

    }

    private List<PlaceDataModel> getPlaceesForCategory(Location loc, String category) {
        NearbySearchRequest nearbySearchRequest =
                PlacesApi.nearbySearchQuery(context, new LatLng(loc.getLatitude(), loc.getLongitude()))
                        .type(PlaceType.valueOf(category.toUpperCase())).radius(1500);

        PlacesSearchResponse placesSearchResponse = nearbySearchRequest.awaitIgnoreError();

        if(placesSearchResponse == null) {
            Log.d("Place Search Error Response", "nullllllllll");
        }

        Log.d("Place Search Response", placesSearchResponse.toString());
        PlacesSearchResult[] results = placesSearchResponse.results;

        return Arrays.stream(results).map(e -> PlaceMapper.map(category, e)).collect(Collectors.toList());
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","******** status *********");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","*********** enabled *********");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","********* disable **********");
    }


    private List<String> getCategories() {
        String[] projection = {
                BaseColumns._ID,
                TaskContract.TaskEntry.COL_TASK_CATEGORY
        };

        String selection = TaskContract.TaskEntry.COL_TASK_STATUS + " = ?";
        String[] selectionArgs = { "OPEN" };


        Cursor cursor = null;
        List<String> categories = null;
        try {
            db = mDbHelper.getReadableDatabase();
            db.beginTransaction();
            cursor = db.query(
                    TaskContract.TaskEntry.TABLE,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    null               // The sort order
            );

            categories = new ArrayList<>();

            while(cursor.moveToNext()) {
                String taskCategory = cursor.getString(
                        cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COL_TASK_CATEGORY));

                categories.add(taskCategory);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.endTransaction();
            db.close();
        }


        return categories;
    }

    public void createPlace(PlaceDataModel place){

        Log.d("Test", "Creating Place>>" + place.toString());

        ContentValues values = new ContentValues();
        values.put(PlaceContract.PlaceEntry.COL_PLACE_ID, place.getId());
        values.put(PlaceContract.PlaceEntry.COL_PLACE_NAME, place.getName());
        values.put(PlaceContract.PlaceEntry.COL_TASK_CATEGORY, place.getCategory());
        values.put(PlaceContract.PlaceEntry.COL_PLACE_ICON_IMAGE_URL, place.getImageUrl());

        Log.d("Test", "Inserting place");

        placeDb = placeDbHelper.getWritableDatabase();
        try {
            long newRowId = placeDb.insertOrThrow(PlaceContract.PlaceEntry.TABLE, null, values);

            Log.d("Test", "Place ID>>" + newRowId);
        } catch (Exception e) {
            Log.d("DB Error:::", e.toString());
        } finally {
            placeDb.close();
        }
    }

    private void truncatePlaceTable() {
        Log.d("Test", "Truncating DB");
        placeDb = placeDbHelper.getWritableDatabase();
        placeDb.beginTransaction();
        try {
            placeDb.execSQL("delete from "+ PlaceContract.PlaceEntry.TABLE);
        } catch (SQLException e) {
            Log.d("DB Truncate Error:::", e.toString());
        } finally {
            placeDb.endTransaction();
            placeDb.close();
        }
    }

}
