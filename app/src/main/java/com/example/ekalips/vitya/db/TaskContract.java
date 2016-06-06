package com.example.ekalips.vitya.db;

/**
 * Created by ekalips on 5/26/16.
 */

import android.provider.BaseColumns;

public class TaskContract {
    public static final String DB_NAME = "com.example.ekalips.vitya.db";
    public static final int DB_VERSION = 2;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";
        public static final String COL_TASK_TITLE = "title";
        public static final String COL_TASK_DATE = "date";
        public static final String COL_TASK_ALARM_ID = "alarm";
    }
}