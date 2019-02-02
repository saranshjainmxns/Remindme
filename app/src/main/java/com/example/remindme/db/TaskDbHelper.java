package com.example.remindme.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TaskDbHelper extends SQLiteOpenHelper {

    public TaskDbHelper(Context context) {
        super(context, TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL DEFAULT 'OPEN', %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT);",
                TaskContract.TaskEntry.TABLE, TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_STATUS, TaskContract.TaskEntry.COL_TASK_NAME, TaskContract.TaskEntry.COL_TASK_CATEGORY
        , TaskContract.TaskEntry.COL_TASK_DESC);
        Log.d("Create Table query", createTable);
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE);
        onCreate(db);
    }
}