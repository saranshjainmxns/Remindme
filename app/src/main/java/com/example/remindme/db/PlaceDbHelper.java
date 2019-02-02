package com.example.remindme.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PlaceDbHelper extends SQLiteOpenHelper {

    public PlaceDbHelper(Context context) {
        super(context, PlaceContract.DB_NAME, null, PlaceContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL UNIQUE, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT);",
                PlaceContract.PlaceEntry.TABLE, PlaceContract.PlaceEntry._ID, PlaceContract.PlaceEntry.COL_PLACE_ID, PlaceContract.PlaceEntry.COL_PLACE_NAME,
                PlaceContract.PlaceEntry.COL_TASK_CATEGORY, PlaceContract.PlaceEntry.COL_PLACE_ICON_IMAGE_URL);
        Log.d("Create Table query", createTable);
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PlaceContract.PlaceEntry.TABLE);
        onCreate(db);
    }
}