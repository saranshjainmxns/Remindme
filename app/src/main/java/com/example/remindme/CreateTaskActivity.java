package com.example.remindme;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindme.db.TaskContract;
import com.example.remindme.db.TaskDbHelper;

public class CreateTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TaskDbHelper mHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        mHelper = new TaskDbHelper(this);
        db = mHelper.getWritableDatabase();

        populateCategorySpinner();

    }

    private void populateCategorySpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.category_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }


    public void createTask(View view){

        Log.d("Test", "Creating task");
        String taskName = ((TextView) findViewById(R.id.task_name)).getText().toString();
        String taskCategory = ((Spinner) findViewById(R.id.category_spinner)).getSelectedItem().toString();
        String taskDesc = ((TextView) findViewById(R.id.task_desc)).getText().toString();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COL_TASK_NAME, taskName);
        values.put(TaskContract.TaskEntry.COL_TASK_CATEGORY, taskCategory);
        values.put(TaskContract.TaskEntry.COL_TASK_DESC, taskDesc);

        long newRowId = db.insert(TaskContract.TaskEntry.TABLE, null, values);

        Log.d("Test", "Task ID" + newRowId);

        finish();
    }

    @Override
    protected void onDestroy() {
        mHelper.close();
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        Log.d("Selected Item", "************"+item+"*****************************");

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
