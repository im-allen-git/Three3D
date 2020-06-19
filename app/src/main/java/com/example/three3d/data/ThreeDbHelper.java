package com.example.three3d.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ThreeDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "threed.db";
    private static final int DATABASE_VERSION = 1;

    public ThreeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + ThreeEntry.TABLE_NAME + " ("
                + ThreeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ThreeEntry.COLUMN_SOURCE_STL_NAME + " TEXT , "
                + ThreeEntry.COLUMN_REAL_STL_NAME + " TEXT , "
                + ThreeEntry.COLUMN_SOURCE_ZIP_STL_NAME + " TEXT , "
                + ThreeEntry.COLUMN_LOCAL_GCODE_NAME + " TEXT , "
                + ThreeEntry.COLUMN_SERVER_ZIP_GCODE_NAME + " TEXT , "
                + ThreeEntry.COLUMN_CREATE_TIME + " TEXT , "
                + ThreeEntry.COLUMN_LOCAL_IMG + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}