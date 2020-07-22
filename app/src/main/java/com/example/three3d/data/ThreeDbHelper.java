package com.example.three3d.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ThreeDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shianzhi.db";
    private static final int DATABASE_VERSION = 1;

    public ThreeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

//        // Create a String that contains the SQL statement to create the pets table
//        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + ThreeEntry.TABLE_NAME + " ("
//                + ThreeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + ThreeEntry.COLUMN_SOURCE_STL_NAME + " TEXT , "
//                + ThreeEntry.COLUMN_REAL_STL_NAME + " TEXT , "
//                + ThreeEntry.COLUMN_SOURCE_ZIP_STL_NAME + " TEXT , "
//                + ThreeEntry.COLUMN_LOCAL_GCODE_NAME + " TEXT , "
//                + ThreeEntry.COLUMN_SERVER_ZIP_GCODE_NAME + " TEXT , "
//                + ThreeEntry.COLUMN_CREATE_TIME + " TEXT , "
//                + ThreeEntry.COLUMN_LOCAL_IMG + " TEXT);";
//
//        // Execute the SQL statement
//        db.execSQL(SQL_CREATE_PETS_TABLE);

        String SQL_CREATE_PRINTER_TABLE = "CREATE TABLE " + ThreePrinterEntry.TABLE_NAME + " ("
                + ThreePrinterEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ThreePrinterEntry.COLUMN_WIFI_URL + " TEXT);";
        db.execSQL(SQL_CREATE_PRINTER_TABLE);


        // user Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " ("
                + UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UserEntry.COLUMN_PASSWORD + " TEXT , "
                + UserEntry.COLUMN_NICK_NAME + " TEXT , "
                + UserEntry.COLUMN_MOBILE + " TEXT , "
                + UserEntry.COLUMN_SEX + " TEXT , "
                + UserEntry.COLUMN_BIRTHDAY + " TEXT , "
                + UserEntry.COLUMN_HEIGHT + " TEXT , "
                + UserEntry.COLUMN_WEIGHT + " TEXT , "
                + UserEntry.COLUMN_WASTE_RATE + " TEXT , "
                + UserEntry.COLUMN_NUMBER + " TEXT , "
                + UserEntry.COLUMN_CREATE_TIME + " TEXT);";
        // user Execute the SQL statement
        db.execSQL(SQL_CREATE_USER_TABLE);




        // binding_user Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_BINDINGUSER_TABLE = "CREATE TABLE " + BindingUserEntry.TABLE_NAME + " ("
                + BindingUserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BindingUserEntry.COLUMN_USER_ID + " TEXT , "
                + BindingUserEntry.COLUMN_BINDING_USERID + " TEXT , "
                + BindingUserEntry.COLUMN_CREATE_TIME + " TEXT);";
        // binding_user Execute the SQL statement
        db.execSQL(SQL_CREATE_BINDINGUSER_TABLE);

        // Equipment Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_EQUIPMENT_TABLE = "CREATE TABLE " + EquipmentEntry.TABLE_NAME + " ("
                + EquipmentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EquipmentEntry.COLUMN_UUID + " TEXT , "
                + EquipmentEntry.COLUMN_NAME + " TEXT , "
                + EquipmentEntry.COLUMN_USER_ID + " TEXT , "
                + EquipmentEntry.COLUMN_ITEM + " TEXT , "
                + EquipmentEntry.COLUMN_UNIT + " TEXT , "
                + EquipmentEntry.COLUMN_TARGET + " TEXT , "
                + EquipmentEntry.COLUMN_CREATE_TIME + " TEXT , "
                + EquipmentEntry.COLUMN_UPDATE_TIME + " TEXT);";
        // Equipment Execute the SQL statement
        db.execSQL(SQL_CREATE_EQUIPMENT_TABLE);

        // weighing_data Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_WEIGHING_DATA_TABLE = "CREATE TABLE " + WeighingdataEntry.TABLE_NAME + " ("
                + WeighingdataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WeighingdataEntry.COLUMN_USER_ID + " TEXT , "
                + WeighingdataEntry.COLUMN_UUID + " TEXT , "
                + WeighingdataEntry.COLUMN_ITEM + " TEXT , "
                + WeighingdataEntry.COLUMN_TYPE + " TEXT , "
                + WeighingdataEntry.COLUMN_WEIGHT + " TEXT , "
                + WeighingdataEntry.COLUMN_CREATE_TIME + " TEXT);";
        // weighing_data Execute the SQL statement
        db.execSQL(SQL_CREATE_WEIGHING_DATA_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}