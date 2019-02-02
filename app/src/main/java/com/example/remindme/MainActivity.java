package com.example.remindme;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.remindme.db.PlaceDbHelper;
import com.example.remindme.db.TaskContract;
import com.example.remindme.db.TaskDbHelper;
import com.example.remindme.job.Util;
import com.example.remindme.services.FinderService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private TaskDbHelper mDbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new TaskDbHelper(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Test", "********* Main activity Start **********");
        String[] projection = {
                BaseColumns._ID,
                TaskContract.TaskEntry.COL_TASK_NAME,
                TaskContract.TaskEntry.COL_TASK_CATEGORY,
                TaskContract.TaskEntry.COL_TASK_DESC
        };

        String selection = TaskContract.TaskEntry.COL_TASK_STATUS + " = ?";
        String[] selectionArgs = { "OPEN" };


        Cursor cursor = null;
        List<TaskDataModel> taskDataModelList = null;
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

            taskDataModelList = new ArrayList<>();

            while(cursor.moveToNext()) {
                String taskName = cursor.getString(
                        cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COL_TASK_NAME));
                String taskCategory = cursor.getString(
                        cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COL_TASK_CATEGORY));
                String taskDesc = cursor.getString(
                        cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COL_TASK_DESC));

                taskDataModelList.add(new TaskDataModel(taskName, taskCategory, taskDesc));
            }
        } catch (IllegalArgumentException e) {
            Log.d("Error>>>", e.toString());
        } finally {
            cursor.close();
            db.endTransaction();
            db.close();
        }


        final ArrayList<String> categories = taskDataModelList.stream().map(e -> e.getName()).collect(Collectors.toCollection(ArrayList::new));

        listView= findViewById(R.id.list_view);


        final CustomAdapter adapter1 = new CustomAdapter(taskDataModelList, getApplicationContext());

        listView.setAdapter(adapter1);
        listView.setOnItemClickListener(adapter1);

//        Util.scheduleJob(getApplicationContext());
        showPermissionDialog();

//        launchFinderService(categories);

    }

    @Override
    protected void onPause() {
        Log.d("Test", "********* Main activity PAUSE **********");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("Test", "********* Main activity STOP **********");
        super.onStop();
    }

    public void createTask(View view) {
        Toast myToast = Toast.makeText(this, "Button CLicked " ,
                Toast.LENGTH_SHORT);
        myToast.show();
        Intent createTaskIntent = new Intent(this, CreateTaskActivity.class);
        startActivity(createTaskIntent);
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    private void launchFinderService(ArrayList<String> categories) {
        Intent mServiceIntent = new Intent(this, FinderService.class);
        mServiceIntent.putStringArrayListExtra ("categories", categories);

        if (!isMyServiceRunning(FinderService.class)) {
            startService(mServiceIntent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
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
}
