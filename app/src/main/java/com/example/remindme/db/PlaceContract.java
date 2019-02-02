package com.example.remindme.db;

import android.provider.BaseColumns;

public class PlaceContract {
    public static final String DB_NAME = "com.vip.remindme.place.db";
    public static final int DB_VERSION = 3;

    public class PlaceEntry implements BaseColumns {
        public static final String TABLE = "places";
        public static final String COL_PLACE_ID= "place_id";
        public static final String COL_PLACE_NAME= "name";
        public static final String COL_TASK_CATEGORY= "category";
        public static final String COL_PLACE_ICON_IMAGE_URL= "image_url";

    }
}