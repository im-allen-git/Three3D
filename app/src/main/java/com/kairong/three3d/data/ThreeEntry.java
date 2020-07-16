package com.kairong.three3d.data;

import android.provider.BaseColumns;

/**
 * Inner class that defines constant values for the pets database table.
 * Each entry in the table represents a single pet.
 */
public final class ThreeEntry implements BaseColumns {

    /** Name of database table for pets */
    public final static String TABLE_NAME = "module";
    public final static String _ID = BaseColumns._ID;

    public final static String COLUMN_SOURCE_STL_NAME ="source_stl";
    public final static String COLUMN_REAL_STL_NAME ="real_stl";
    public final static String COLUMN_SOURCE_ZIP_STL_NAME ="source_zip_stl";
    public final static String COLUMN_LOCAL_GCODE_NAME ="local_gcode";
    public final static String COLUMN_SERVER_ZIP_GCODE_NAME ="server_zip_gcode";
    public final static String COLUMN_CREATE_TIME ="create_time";
    public final static String COLUMN_LOCAL_IMG ="local_img";

    public final static String COLUMN_LENGTH ="g_length";
    public final static String COLUMN_WIDTH ="g_width";
    public final static String COLUMN_HEIGHT ="g_height";
    public final static String COLUMN_SIZE ="g_size";
    public final static String COLUMN_MATERIAL ="material";
    public final static String COLUMN_EXE_TIME ="exe_time";
    public final static String COLUMN_UPLOAD_FLAG ="upload_flag";

}