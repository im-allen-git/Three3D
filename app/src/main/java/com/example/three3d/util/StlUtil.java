package com.example.three3d.util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.three3d.data.BindingUserEntry;
import com.example.three3d.data.EquipmentEntry;
import com.example.three3d.data.ThreeDbHelper;
import com.example.three3d.data.ThreeEntry;
import com.example.three3d.data.ThreePrinterEntry;
import com.example.three3d.data.UserEntry;
import com.example.three3d.data.WeighingdataEntry;
import com.example.three3d.pojo.BindingUserPojo;
import com.example.three3d.pojo.EquipmentPojo;
import com.example.three3d.pojo.StlGcode;
import com.example.three3d.pojo.UserPojo;
import com.example.three3d.pojo.WeighingdataPojo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StlUtil {


    public static volatile String ESP_8266_URL = null;

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
                    serverZipGcodeName, localGcodeName, createTime, localImg,"0","0","0","0","0");
            stlDataBaseMap.put(stlGcode.getRealStlName(), stlGcode);
        }
    }


    private static SQLiteDatabase getDbByContext(Context context) {
        ThreeDbHelper mDbHelper = new ThreeDbHelper(context);
        return mDbHelper.getReadableDatabase();
    }


    public static String getFormatTime(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(date);
    }


    /**
     * 插入打印机的wifi链接
     *
     * @param context
     * @param url
     * @return
     */
    public static long savePrinterUrl(Context context, String url) {
        SQLiteDatabase db = getDbByContext(context);
        ContentValues values = new ContentValues();
        values.put(ThreePrinterEntry.COLUMN_WIFI_URL, url);
        long newRowId = db.insert(ThreePrinterEntry.TABLE_NAME, null, values);
        ESP_8266_URL = url;
        return newRowId;
    }


    public static void getPrinterUrl(Context context) {
        SQLiteDatabase db = getDbByContext(context);
        Cursor cursor = db.query(ThreePrinterEntry.TABLE_NAME, null, null, null, null, null, null);
        int idIndex = cursor.getColumnIndex(ThreePrinterEntry._ID);
        int urlIndex = cursor.getColumnIndex(ThreePrinterEntry.COLUMN_WIFI_URL);

        if (cursor.moveToNext()) {
            int id = cursor.getInt(idIndex);
            String url = cursor.getString(urlIndex);
            ESP_8266_URL = url;
        } else {
            ESP_8266_URL = null;
        }
    }


    public static void updatePrinterUrl(Context context, String url) {
        SQLiteDatabase db = getDbByContext(context);
        if (url != null && url.length() > 0) {
            ContentValues values = new ContentValues();
            values.put(ThreePrinterEntry.COLUMN_WIFI_URL, url);
            String whereClause = ThreePrinterEntry.COLUMN_WIFI_URL + " = ?";
            String[] whereArgs = new String[]{ESP_8266_URL};
            db.update(ThreePrinterEntry.TABLE_NAME, values, whereClause, whereArgs);
            ESP_8266_URL = url;
        }
    }


    /**
     * 保存注册用户数据
     *
     * @param context
     * @param userPojo
     * @return
     */
    static long saveUserDataBase(Context context, UserPojo userPojo) {
        SQLiteDatabase db = getDbByContext(context);

        ContentValues values = new ContentValues();
//        values.put(UserEntry.COLUMN_USER_ID, userPojo.getUserId());
        values.put(UserEntry.COLUMN_NICK_NAME, userPojo.getNickName());
        values.put(UserEntry.COLUMN_MOBILE, userPojo.getMobile());
        values.put(UserEntry.COLUMN_CREATE_TIME, StlUtil.getFormatTime(new Date()));


        long newRowId = db.insert(UserEntry.TABLE_NAME, null, values);



        return newRowId;
    }

    /**
     * 用户信息更新
     *
     * @param context
     * @param
     */
    public static void updateUserInfoDataBase(Context context, UserPojo userPojo) {
        SQLiteDatabase db = getDbByContext(context);

            ContentValues values = new ContentValues();

            values.put(UserEntry.COLUMN_NICK_NAME, userPojo.getNickName());
//            values.put(UserEntry.COLUMN_PASSWORD, userPojo.getPassword());
//            values.put(UserEntry.COLUMN_MOBILE, userPojo.getMobile());
            values.put(UserEntry.COLUMN_SEX, userPojo.getSex());
            values.put(UserEntry.COLUMN_BIRTHDAY, userPojo.getBirthday());
            values.put(UserEntry.COLUMN_HEIGHT, userPojo.getHeight());
            values.put(UserEntry.COLUMN_WEIGHT, userPojo.getWeight());
            values.put(UserEntry.COLUMN_WASTE_RATE, userPojo.getWasteRate());
            values.put(UserEntry.COLUMN_NUMBER, userPojo.getNumber());
            String whereClause = UserEntry.COLUMN_USER_ID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(userPojo.getUserId())};
            db.update(UserEntry.TABLE_NAME, values, whereClause, whereArgs);


    }

    /**
     * 保存群组共享用户数据
     *
     * @param context
     * @param bindingUserPojo
     * @return
     */
    static long saveBindingUserDataBase(Context context, BindingUserPojo bindingUserPojo) {
        SQLiteDatabase db = getDbByContext(context);

        ContentValues values = new ContentValues();
        values.put(BindingUserEntry.COLUMN_USER_ID, bindingUserPojo.getUserId());
        values.put(BindingUserEntry.COLUMN_BINDING_USERID, bindingUserPojo.getBindingUserid());

        long newRowId = db.insert(BindingUserEntry.TABLE_NAME, null, values);


        return newRowId;
    }

    /**
     * 删除群组共享用户数据
     *
     * @param context
     * @param userId
     * @return
     */
    public static void deleteBindingUserDataBase(Context context, String userId) {
        SQLiteDatabase db = getDbByContext(context);
        String whereClause = BindingUserEntry.COLUMN_USER_ID + " = ?";
        String[] whereArgs = {userId};
        db.delete(BindingUserEntry.TABLE_NAME, whereClause, whereArgs);
    }


    /**
     * 保存设备数据
     *
     * @param context
     * @param equipmentPojo
     * @return
     */
    static long saveEquipmentDataBase(Context context, EquipmentPojo equipmentPojo) {
        SQLiteDatabase db = getDbByContext(context);

        ContentValues values = new ContentValues();
        values.put(EquipmentEntry.COLUMN_MAC, equipmentPojo.getMac());
        values.put(EquipmentEntry.COLUMN_UUID, equipmentPojo.getUuId());
        values.put(EquipmentEntry.COLUMN_USER_ID, equipmentPojo.getName());
        values.put(EquipmentEntry.COLUMN_USER_ID, equipmentPojo.getUserId());
        values.put(EquipmentEntry.COLUMN_USER_ID, equipmentPojo.getItem());
        values.put(EquipmentEntry.COLUMN_USER_ID, equipmentPojo.getUnit());
        values.put(EquipmentEntry.COLUMN_USER_ID, equipmentPojo.getTarget());

        long newRowId = db.insert(EquipmentEntry.TABLE_NAME, null, values);


        return newRowId;
    }

    /**
     * 删除设备数据
     *
     * @param context
     * @param uuId
     * @return
     */
    public static void deleteEquipmentDataBase(Context context, String uuId) {
        SQLiteDatabase db = getDbByContext(context);
        String whereClause = EquipmentEntry.COLUMN_UUID + " = ?";
        String[] whereArgs = {uuId};
        db.delete(EquipmentEntry.TABLE_NAME, whereClause, whereArgs);
    }


    /**
     * 设备信息修改
     *
     * @param context
     * @param
     */
    public static void updateEquipment(Context context, EquipmentPojo equipmentPojo) {
        SQLiteDatabase db = getDbByContext(context);

        ContentValues values = new ContentValues();
        values.put(EquipmentEntry.COLUMN_ITEM, equipmentPojo.getItem());
        values.put(EquipmentEntry.COLUMN_UNIT, equipmentPojo.getUnit());
        values.put(EquipmentEntry.COLUMN_TARGET, equipmentPojo.getTarget());

        String whereClause = EquipmentEntry.COLUMN_UUID + " = ? and " +EquipmentEntry.COLUMN_USER_ID +" = ? and "+EquipmentEntry.COLUMN_ITEM+" = ? ";
        String[] whereArgs = new String[]{String.valueOf(equipmentPojo.getUuId()),String.valueOf(equipmentPojo.getUserId())
        ,String.valueOf(equipmentPojo.getItem())};
        db.update(EquipmentEntry.TABLE_NAME, values, whereClause, whereArgs);
//            stlDataBaseMap.put(realStlName, stlGcode);

    }


    /**
     * 保存设备数据
     *
     * @param context
     * @param weighingdataPojo
     * @return
     */
    static long saveWeighingDataBase(Context context, WeighingdataPojo weighingdataPojo) {
        SQLiteDatabase db = getDbByContext(context);

        ContentValues values = new ContentValues();
        values.put(WeighingdataEntry._ID, weighingdataPojo.getId());
        values.put(WeighingdataEntry.COLUMN_USER_ID, weighingdataPojo.getUserId());
        values.put(WeighingdataEntry.COLUMN_MAC, weighingdataPojo.getMac());
        values.put(WeighingdataEntry.COLUMN_ITEM, weighingdataPojo.getItem());
        values.put(WeighingdataEntry.COLUMN_TYPE, weighingdataPojo.getType());
        values.put(WeighingdataEntry.COLUMN_WEIGHT, weighingdataPojo.getWeight());
        values.put(WeighingdataEntry.COLUMN_CREATE_TIME, weighingdataPojo.getCreateTime());
        values.put(WeighingdataEntry.COLUMN_WASTE_RATE, "1");
        values.put(WeighingdataEntry.COLUMN_NUMBER, "1");
        values.put(WeighingdataEntry.COLUMN_STATUS, "1");
        values.put(WeighingdataEntry.COLUMN_MODIFY_TIME, StlUtil.getFormatTime(new Date()));

        long newRowId = db.insert(WeighingdataEntry.TABLE_NAME, null, values);

        return newRowId;
    }


    /**
     * 设备信息修改
     *
     * @param context
     * @param
     */
    public static void updateWeighingData(Context context, WeighingdataPojo weighingdataPojo) {
        SQLiteDatabase db = getDbByContext(context);

        // "1:10;2:20;3:30"
        String[] weightArry =  weighingdataPojo.getWeightStr().split(";");
        //String数组转List
        List<String> weightList= Arrays.asList(weightArry);
        for(String ws:weightList){

            ContentValues values = new ContentValues();
            values.put(WeighingdataEntry.COLUMN_TYPE, weighingdataPojo.getType());
            values.put(WeighingdataEntry.COLUMN_NUMBER, weighingdataPojo.getNumber());
            values.put(WeighingdataEntry.COLUMN_WASTE_RATE, weighingdataPojo.getWasteRate());
            values.put(WeighingdataEntry.COLUMN_WEIGHT, String.valueOf(ws.split(":")[1]));
            values.put(WeighingdataEntry.COLUMN_MODIFY_TIME, StlUtil.getFormatTime(new Date()));

            String whereClause = WeighingdataEntry._ID + " = ? ";
            String[] whereArgs = new String[]{String.valueOf(ws.split(":")[0])};
            db.update(WeighingdataEntry.TABLE_NAME, values, whereClause, whereArgs);
        }


    }


    /**
     * 逻辑删除设备信息单条
     *
     * @param context
     * @param
     */
    public static void updateDelWeighingData(Context context, WeighingdataPojo weighingdataPojo) {
        SQLiteDatabase db = getDbByContext(context);

        ContentValues values = new ContentValues();
        values.put(WeighingdataEntry.COLUMN_STATUS, "2");

        String whereClause = WeighingdataEntry._ID + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(weighingdataPojo.getId())};
        db.update(WeighingdataEntry.TABLE_NAME, values, whereClause, whereArgs);

    }

    /**
     * 逻辑删除设备信息多条
     *
     * @param context
     * @param
     */
    public static void updateDelWeighingAllData(Context context, WeighingdataPojo weighingdataPojo) {
        SQLiteDatabase db = getDbByContext(context);

        // "1:2:3"
        String[] weightArry =  weighingdataPojo.getIdAllStr().split(":");
        //String数组转List
        List<String> idList= Arrays.asList(weightArry);
        for(String idS:idList){

            ContentValues values = new ContentValues();
            values.put(WeighingdataEntry.COLUMN_STATUS, "2");

            String whereClause = WeighingdataEntry._ID + " = ? ";
            String[] whereArgs = new String[]{String.valueOf(idS)};
            db.update(WeighingdataEntry.TABLE_NAME, values, whereClause, whereArgs);

        }
    }


    /**
     * 获取用户数据
     *
     * @param context
     * userID
     */
    public static List<Map<String, Object>>  getUserInfoData(Context context,String userId) {
        data_list.clear();
        SQLiteDatabase db = getDbByContext(context);
        String[] whereArgs = new String[]{userId};
        String whereClause = UserEntry.COLUMN_USER_ID + " = ? ";
        Cursor cursor = db.query(UserEntry.TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        while (cursor.moveToNext()) {

            Map<String, Object> stlMap = new HashMap<>();
            stlMap.put("userId", cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_USER_ID)));
            stlMap.put("nickName", cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_NICK_NAME)));
            stlMap.put("password", cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_PASSWORD)));
            stlMap.put("mobile", cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_MOBILE)));
            stlMap.put("sex", cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_SEX)));
            stlMap.put("birthday", cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_BIRTHDAY)));
            stlMap.put("height", cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_HEIGHT)));
            stlMap.put("weight", cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_WEIGHT)));
            stlMap.put("wasteRate", cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_WASTE_RATE)));
            stlMap.put("number", cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_NUMBER)));
            stlMap.put("createTime", cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_CREATE_TIME)));

            data_list.add(stlMap);
        }
        return data_list;
    }

    /**
     * 检查用户是否注册
     *
     * @param context
     * userID
     */
    public static int  checkUserIdExist(Context context,String mobile) {
        data_list.clear();
        int userId = 0;
        SQLiteDatabase db = getDbByContext(context);
        String[] whereArgs = new String[]{mobile};
        String whereClause = UserEntry.COLUMN_MOBILE + " = ? ";
        Cursor cursor = db.query(UserEntry.TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        while (cursor.moveToNext()) {

            userId =cursor.getInt(cursor.getColumnIndex(UserEntry.COLUMN_USER_ID));

        }
        return userId;
    }

    /**
     * 获取最新插入数据的自增长主键ID
     *
     * @param context
     * userID
     */
    public static int  getLastInsertRowid(Context context) {

        SQLiteDatabase db = getDbByContext(context);

        String sql = "select last_insert_rowid() from " + UserEntry.TABLE_NAME ;
        Cursor cursor = db.rawQuery(sql, null);
        int userId = -1;
        if(cursor.moveToFirst()){
            userId = cursor.getInt(0);
        }
        return userId;

    }


    /**
     * 获取用户数据
     *
     * @param context
     * userID
     */
    public static List<Map<String, Object>>  getBindingUserList(Context context,String userId) {
        data_list.clear();
        SQLiteDatabase db = getDbByContext(context);
        String[] whereArgs = new String[]{userId};
        String whereClause = BindingUserEntry.COLUMN_USER_ID + " = ? ";
        Cursor cursor = db.query(BindingUserEntry.TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        while (cursor.moveToNext()) {

            Map<String, Object> stlMap = new HashMap<>();
            stlMap.put("userId", cursor.getString(cursor.getColumnIndex(BindingUserEntry.COLUMN_USER_ID)));
            stlMap.put("userId", cursor.getString(cursor.getColumnIndex(BindingUserEntry.COLUMN_BINDING_USERID)));
            stlMap.put("createTime", cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_CREATE_TIME)));

            data_list.add(stlMap);
        }
        return data_list;
    }




    /**
     * 获取设备数据
     *
     * @param context
     * userID
     */
    public static List<Map<String, Object>>  getEquipmentData(Context context,String userId) {
        data_list.clear();
        SQLiteDatabase db = getDbByContext(context);
        String[] whereArgs = new String[]{userId};
        String whereClause = EquipmentEntry.COLUMN_USER_ID + " = ? ";
        Cursor cursor = db.query(EquipmentEntry.TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        while (cursor.moveToNext()) {

            Map<String, Object> stlMap = new HashMap<>();
            stlMap.put("mac", cursor.getString(cursor.getColumnIndex(EquipmentEntry.COLUMN_MAC)));
            stlMap.put("uuId", cursor.getString(cursor.getColumnIndex(EquipmentEntry.COLUMN_UUID)));
            stlMap.put("name", cursor.getString(cursor.getColumnIndex(EquipmentEntry.COLUMN_NAME)));
            stlMap.put("userId", cursor.getString(cursor.getColumnIndex(EquipmentEntry.COLUMN_USER_ID)));
            stlMap.put("item", cursor.getString(cursor.getColumnIndex(EquipmentEntry.COLUMN_ITEM)));
            stlMap.put("unit", cursor.getString(cursor.getColumnIndex(EquipmentEntry.COLUMN_UNIT)));
            stlMap.put("target", cursor.getString(cursor.getColumnIndex(EquipmentEntry.COLUMN_TARGET)));
            stlMap.put("createTime", cursor.getString(cursor.getColumnIndex(EquipmentEntry.COLUMN_CREATE_TIME)));
            stlMap.put("updateTime", cursor.getString(cursor.getColumnIndex(EquipmentEntry.COLUMN_UPDATE_TIME)));
            stlMap.put("ipAddress", cursor.getString(cursor.getColumnIndex(EquipmentEntry.COLUMN_IP_ADDRESS)));
            stlMap.put("onlineType", cursor.getString(cursor.getColumnIndex(EquipmentEntry.COLUMN_ONLINE_TYPE)));

            data_list.add(stlMap);
        }
        return data_list;
    }

    /**
     * 获取设备数据
     *
     * @param context
     * userID
     */
    public static List<Map<String, Object>>  getWeightingData(Context context,String userId) {
        data_list.clear();
        SQLiteDatabase db = getDbByContext(context);
        String[] whereArgs = new String[]{userId};
        String whereClause = WeighingdataEntry.COLUMN_USER_ID + " = ? ";
        Cursor cursor = db.query(WeighingdataEntry.TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        while (cursor.moveToNext()) {

            Map<String, Object> stlMap = new HashMap<>();
            stlMap.put("id", cursor.getString(cursor.getColumnIndex(WeighingdataEntry._ID)));
            stlMap.put("userId", cursor.getString(cursor.getColumnIndex(WeighingdataEntry.COLUMN_USER_ID)));
            stlMap.put("mac", cursor.getString(cursor.getColumnIndex(WeighingdataEntry.COLUMN_MAC)));
            stlMap.put("item", cursor.getString(cursor.getColumnIndex(WeighingdataEntry.COLUMN_ITEM)));
            stlMap.put("type", cursor.getString(cursor.getColumnIndex(WeighingdataEntry.COLUMN_TYPE)));
            stlMap.put("weight", cursor.getString(cursor.getColumnIndex(WeighingdataEntry.COLUMN_WEIGHT)));
            stlMap.put("createTime", cursor.getString(cursor.getColumnIndex(WeighingdataEntry.COLUMN_CREATE_TIME)));
            stlMap.put("wasteRate", cursor.getString(cursor.getColumnIndex(WeighingdataEntry.COLUMN_WASTE_RATE)));
            stlMap.put("number", cursor.getString(cursor.getColumnIndex(WeighingdataEntry.COLUMN_NUMBER)));
            stlMap.put("modifyTime", cursor.getString(cursor.getColumnIndex(WeighingdataEntry.COLUMN_MODIFY_TIME)));
            stlMap.put("status", cursor.getString(cursor.getColumnIndex(WeighingdataEntry.COLUMN_STATUS)));

            data_list.add(stlMap);
        }
        return data_list;
    }

}
