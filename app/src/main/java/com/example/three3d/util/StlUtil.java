package com.example.three3d.util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.three3d.data.ThreeDbHelper;
import com.example.three3d.data.ThreeEntry;
import com.example.three3d.pojo.StlGcode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StlUtil {
    /**
     * 原始保存的stl数据
     */
    public static volatile Map<String, StlGcode> stlMap = new HashMap<>();

    /**
     * 从数据库读取和后续更新的数据
     */
    public static volatile Map<String, StlGcode> stlDataBaseMap = new HashMap<>();

    private static volatile List<Map<String, Object>> data_list = new ArrayList<>();

    public static List<Map<String, Object>> getDataList() {
        data_list.clear();
        for (Map.Entry<String, StlGcode> stlGcodeEntry : stlDataBaseMap.entrySet()) {
            Map<String, Object> stlMap = new HashMap<>();
            stlMap.put("id", stlGcodeEntry.getValue().getId());
            stlMap.put("sourceStlName", stlGcodeEntry.getValue().getSourceStlName());
            stlMap.put("realStlName", stlGcodeEntry.getValue().getRealStlName());
            stlMap.put("createTime", stlGcodeEntry.getValue().getCreateTime());
            stlMap.put("localImg", stlGcodeEntry.getValue().getLocalImg());
            data_list.add(stlMap);
        }
        if (data_list.size() == 0) {
            return null;
        }
        return data_list;
    }

    /**
     * 更新module数据库数据
     *
     * @param context
     * @param realStlName
     */
    public static void updateModuleDataBase(Context context, String realStlName) {
        SQLiteDatabase db = getDbByContext(context);
        if (stlMap.containsKey(realStlName)) {
            StlGcode stlGcode = stlMap.get(realStlName);
            ContentValues values = new ContentValues();
            // values.put(ThreeEntry._ID, stlGcode.getId());
            values.put(ThreeEntry.COLUMN_SOURCE_STL_NAME, stlGcode.getSourceStlName());
            values.put(ThreeEntry.COLUMN_REAL_STL_NAME, stlGcode.getRealStlName());
            values.put(ThreeEntry.COLUMN_SOURCE_ZIP_STL_NAME, stlGcode.getSourceZipStlName());
            values.put(ThreeEntry.COLUMN_LOCAL_GCODE_NAME, stlGcode.getLocalGcodeName());
            values.put(ThreeEntry.COLUMN_SERVER_ZIP_GCODE_NAME, stlGcode.getServerZipGcodeName());
            values.put(ThreeEntry.COLUMN_CREATE_TIME, stlGcode.getCreateTime());
            values.put(ThreeEntry.COLUMN_LOCAL_IMG, stlGcode.getLocalImg());
            String whereClause = ThreeEntry._ID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(stlGcode.getId())};
            db.update(ThreeEntry.TABLE_NAME, values, whereClause, whereArgs);
            stlDataBaseMap.put(realStlName, stlGcode);
        }
    }

    /**
     * 保存模型数据
     *
     * @param context
     * @param stlGcode
     * @return
     */
    static long saveModuleDataBase(Context context, StlGcode stlGcode) {
        SQLiteDatabase db = getDbByContext(context);

        ContentValues values = new ContentValues();
        values.put(ThreeEntry.COLUMN_SOURCE_STL_NAME, stlGcode.getSourceStlName());
        values.put(ThreeEntry.COLUMN_REAL_STL_NAME, stlGcode.getRealStlName());
        values.put(ThreeEntry.COLUMN_SOURCE_ZIP_STL_NAME, stlGcode.getSourceZipStlName());
        values.put(ThreeEntry.COLUMN_LOCAL_GCODE_NAME, "");
        values.put(ThreeEntry.COLUMN_SERVER_ZIP_GCODE_NAME, "");
        values.put(ThreeEntry.COLUMN_CREATE_TIME, stlGcode.getCreateTime());
        values.put(ThreeEntry.COLUMN_LOCAL_IMG, stlGcode.getLocalImg());
        long newRowId = db.insert(ThreeEntry.TABLE_NAME, null, values);
        stlDataBaseMap.put(stlGcode.getRealStlName(), stlGcode);

        return newRowId;
    }


    public static void deleteModuleDataBase(Context context, String fileName) {
        SQLiteDatabase db = getDbByContext(context);
        String whereClause = ThreeEntry.COLUMN_REAL_STL_NAME + " = ?";
        String[] whereArgs = {fileName};
        db.delete(ThreeEntry.TABLE_NAME, whereClause, whereArgs);
    }


    /**
     * 获取模型数据
     *
     * @param context
     */
    public static void getModuleDataBase(Context context) {
        SQLiteDatabase db = getDbByContext(context);
        Cursor cursor = db.query(ThreeEntry.TABLE_NAME, null, null, null, null, null, null);

        int idIndex = cursor.getColumnIndex(ThreeEntry._ID);
        int sourceStlNameIndex = cursor.getColumnIndex(ThreeEntry.COLUMN_SOURCE_STL_NAME);
        int realStlNameIndex = cursor.getColumnIndex(ThreeEntry.COLUMN_REAL_STL_NAME);
        int sourceZipStlNameIndex = cursor.getColumnIndex(ThreeEntry.COLUMN_SOURCE_ZIP_STL_NAME);
        int serverZipGcodeNameIndex = cursor.getColumnIndex(ThreeEntry.COLUMN_SERVER_ZIP_GCODE_NAME);
        int localGcodeNameIndex = cursor.getColumnIndex(ThreeEntry.COLUMN_LOCAL_GCODE_NAME);
        int createTimeIndex = cursor.getColumnIndex(ThreeEntry.COLUMN_CREATE_TIME);
        int localImgIndex = cursor.getColumnIndex(ThreeEntry.COLUMN_LOCAL_IMG);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(idIndex);
            String sourceStlName = cursor.getString(sourceStlNameIndex);
            String realStlName = cursor.getString(realStlNameIndex);
            String sourceZipStlName = cursor.getString(sourceZipStlNameIndex);
            String serverZipGcodeName = cursor.getString(serverZipGcodeNameIndex);
            String localGcodeName = cursor.getString(localGcodeNameIndex);
            String createTime = cursor.getString(createTimeIndex);
            String localImg = cursor.getString(localImgIndex);
            StlGcode stlGcode = new StlGcode(id, sourceStlName, realStlName, sourceZipStlName,
                    serverZipGcodeName, localGcodeName, createTime, localImg);
            stlDataBaseMap.put(stlGcode.getRealStlName(), stlGcode);
        }
    }


    private static SQLiteDatabase getDbByContext(Context context) {
        ThreeDbHelper mDbHelper = new ThreeDbHelper(context);
        return mDbHelper.getReadableDatabase();
    }


    public static String getFormatTime(Date date){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(date);
    }


}
