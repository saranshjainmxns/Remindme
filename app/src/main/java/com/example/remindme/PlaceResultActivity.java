package com.example.remindme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.remindme.db.PlaceContract;
import com.example.remindme.db.PlaceDbHelper;
import com.example.remindme.db.TaskContract;
import com.example.remindme.mapper.PlaceMapper;
import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlaceResultActivity extends AppCompatActivity {

    public static final String API_KEY = "AIzaSyAzzvLxqgKxiohWzYFPYl1gXzJY_-6sgZc";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SQLiteDatabase db;
    private GeoApiContext context;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_result);

        Bundle extras = getIntent().getExtras();
        category = extras.getString("category");


        PlaceDbHelper placeDbHelper = new PlaceDbHelper(this);
        db = placeDbHelper.getReadableDatabase();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getPlaceList());

        mRecyclerView.setAdapter(mAdapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(PlaceDataModel model, View v) {
                Log.i("LOG_TAG", " Clicked on Item " + model.getName());
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr="+model.lat+","+model.lng));
                startActivity(intent);
            }
        });

    }

    private ArrayList<PlaceDataModel> getPlaceList() {

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        showPermissionDialog();
        @SuppressLint("MissingPermission")
        Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(lastKnownLocation == null) {
            Log.d("PLaceResultActivuty", "lastKnownLocation == null");
            return new ArrayList<>();
        }

        return getPlacesForCategory(lastKnownLocation, category);

    }


    private ArrayList<PlaceDataModel> getPlacesForCategory(Location loc, String category) {
        context = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();

        Log.d("PlaceResultActivity", loc.toString());
        Log.d("PlaceResultActivity:: Category===", category);
        NearbySearchRequest nearbySearchRequest =
                PlacesApi.nearbySearchQuery(context, new LatLng(loc.getLatitude(), loc.getLongitude()))
                        .type(PlaceType.valueOf(category.toUpperCase())).radius(1500);

        PlacesSearchResponse placesSearchResponse = nearbySearchRequest.awaitIgnoreError();

        if(placesSearchResponse == null) {
            Log.d("Place Search Error Response", "nullllllllll");
            return new ArrayList<>();
        }

        Log.d("Place Search Response", placesSearchResponse.toString());
        PlacesSearchResult[] results = placesSearchResponse.results;

        return Arrays.stream(results).map(e -> PlaceMapper.map(category, e)).collect(Collectors.toCollection(ArrayList::new));
    }

    private void showPermissionDialog() {
        if (!checkPermission(this)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
                    100);
        }
    }

    private boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            "Permission granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this,
                            "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private ArrayList<PlaceDataModel> getPlaceListFromDB() {

        Log.d("PlaceResultActivity", "Feting placerss");
        String[] projection = {
                PlaceContract.PlaceEntry.COL_PLACE_ID,
                PlaceContract.PlaceEntry.COL_PLACE_NAME,
                PlaceContract.PlaceEntry.COL_TASK_CATEGORY,
                PlaceContract.PlaceEntry.COL_PLACE_ICON_IMAGE_URL
        };


        Cursor cursor = null;
        ArrayList<PlaceDataModel> taskDataModelList = null;
        try {
            db.beginTransaction();
            Log.d("DB query", "Querting");

            String selection = TaskContract.TaskEntry.COL_TASK_CATEGORY + " = ?";
            String[] selectionArgs = { category };


            cursor = db.query(
                    PlaceContract.PlaceEntry.TABLE,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    null               // The sort order
            );

            taskDataModelList = new ArrayList<>();

            Log.d("Cursor", "Iterating cursor");

            while(cursor.moveToNext()) {
                String placeeId = cursor.getString(
                        cursor.getColumnIndexOrThrow(PlaceContract.PlaceEntry.COL_PLACE_ID));
                String placeName = cursor.getString(
                        cursor.getColumnIndexOrThrow(PlaceContract.PlaceEntry.COL_PLACE_NAME));
                String palceCategory = cursor.getString(
                        cursor.getColumnIndexOrThrow(PlaceContract.PlaceEntry.COL_TASK_CATEGORY));
                String imageUrl = cursor.getString(
                        cursor.getColumnIndexOrThrow(PlaceContract.PlaceEntry.COL_PLACE_ICON_IMAGE_URL));

                PlaceDataModel model = new PlaceDataModel(placeeId, placeName, palceCategory, imageUrl);
                Log.d("Place Model", model.toString());
                taskDataModelList.add(model);
            }

        } catch (IllegalArgumentException e) {
            Log.d("Errrorrrrr", e.toString());
        } finally {
            cursor.close();
            db.endTransaction();
            db.close();
        }
        return taskDataModelList;
    }


    /*@Override
    public void onLocationChanged(Location location) {
        List<PlaceDataModel> placesForCategory = getPlacesForCategory(location, category);

    }

    private List<PlaceDataModel> getPlacesForCategory(Location loc, String category) {
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

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }*/
}
