package com.example.remindme.db;

import android.provider.BaseColumns;

public class TaskContract {
    public static final String DB_NAME = "com.vip.remindme.task.db";
    public static final int DB_VERSION = 3;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";
        public static final String COL_TASK_NAME= "name";
        public static final String COL_TASK_CATEGORY= "category";
        public static final String COL_TASK_DESC= "desc";
        public static final String COL_TASK_STATUS= "status";

    }
}