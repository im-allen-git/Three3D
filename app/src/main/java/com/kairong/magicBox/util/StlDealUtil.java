package com.kairong.magicBox.util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kairong.magicBox.config.PrinterConfig;
import com.kairong.magicBox.data.ThreeDbHelper;
import com.kairong.magicBox.data.ThreeEntry;
import com.kairong.magicBox.data.ThreePrinterEntry;
import com.kairong.magicBox.pojo.PrinterGcodeInfo;
import com.kairong.magicBox.pojo.StlGcode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * stl和gcodeg工具类
 */
public class StlDealUtil {


    /**
     * 原始保存的stl数据
     */
    public static volatile Map<String, StlGcode> stlMap = new HashMap<>();

    /**
     * APP自带gcode的map
     */
    public static Map<String, StlGcode> localMapStl = new HashMap<>();

    /**
     * APP自带gcode集合
     */
    private static List<StlGcode> localStlList = new ArrayList<>();


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

            stlMap.put("x", stlGcodeEntry.getValue().getLength());
            stlMap.put("y", stlGcodeEntry.getValue().getWidth());
            stlMap.put("z", stlGcodeEntry.getValue().getHeight());
            stlMap.put("size", stlGcodeEntry.getValue().getSize());
            stlMap.put("material", stlGcodeEntry.getValue().getMaterial());
            stlMap.put("exeTimeStr", IOUtil.getTimeStr(stlGcodeEntry.getValue().getExeTime()));

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

            values.put(ThreeEntry.COLUMN_LENGTH, stlGcode.getLength());
            values.put(ThreeEntry.COLUMN_WIDTH, stlGcode.getWidth());
            values.put(ThreeEntry.COLUMN_HEIGHT, stlGcode.getHeight());
            values.put(ThreeEntry.COLUMN_SIZE, stlGcode.getSize());
            values.put(ThreeEntry.COLUMN_MATERIAL, stlGcode.getMaterial());
            values.put(ThreeEntry.COLUMN_EXE_TIME, String.valueOf(stlGcode.getExeTime()));

            values.put(ThreeEntry.COLUMN_UPLOAD_FLAG, String.valueOf(stlGcode.getFlag()));


            String whereClause = ThreeEntry._ID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(stlGcode.getId())};
            db.update(ThreeEntry.TABLE_NAME, values, whereClause, whereArgs);

            stlDataBaseMap.remove(realStlName);
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

        values.put(ThreeEntry.COLUMN_LENGTH, "0");
        values.put(ThreeEntry.COLUMN_WIDTH, "0");
        values.put(ThreeEntry.COLUMN_HEIGHT, "0");
        values.put(ThreeEntry.COLUMN_SIZE, "0");
        values.put(ThreeEntry.COLUMN_MATERIAL, "0");
        values.put(ThreeEntry.COLUMN_EXE_TIME, "0");
        values.put(ThreeEntry.COLUMN_UPLOAD_FLAG, "0");

        long newRowId = db.insert(ThreeEntry.TABLE_NAME, null, values);
        stlGcode.setId(newRowId);
        stlDataBaseMap.put(stlGcode.getRealStlName(), stlGcode);
        db.close();
        return newRowId;
    }


    public static void deleteModuleDataBase(Context context, String fileName) {
        SQLiteDatabase db = getDbByContext(context);
        String whereClause = ThreeEntry.COLUMN_REAL_STL_NAME + " = ?";
        String[] whereArgs = {fileName};
        db.delete(ThreeEntry.TABLE_NAME, whereClause, whereArgs);
        db.close();
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

        int lengthIndex = cursor.getColumnIndex(ThreeEntry.COLUMN_LENGTH);
        int widthIndex = cursor.getColumnIndex(ThreeEntry.COLUMN_WIDTH);
        int heigthIndex = cursor.getColumnIndex(ThreeEntry.COLUMN_HEIGHT);
        int sizeIndex = cursor.getColumnIndex(ThreeEntry.COLUMN_SIZE);
        int materialIndex = cursor.getColumnIndex(ThreeEntry.COLUMN_MATERIAL);
        int timeIndex = cursor.getColumnIndex(ThreeEntry.COLUMN_EXE_TIME);

        int uploadFlagIndex = cursor.getColumnIndex(ThreeEntry.COLUMN_UPLOAD_FLAG);


        while (cursor.moveToNext()) {
            int id = cursor.getInt(idIndex);
            String sourceStlName = cursor.getString(sourceStlNameIndex);
            String realStlName = cursor.getString(realStlNameIndex);
            String sourceZipStlName = cursor.getString(sourceZipStlNameIndex);
            String serverZipGcodeName = cursor.getString(serverZipGcodeNameIndex);
            String localGcodeName = cursor.getString(localGcodeNameIndex);
            String createTime = cursor.getString(createTimeIndex);
            String localImg = cursor.getString(localImgIndex);

            String lengthStr = cursor.getString(lengthIndex);
            String widthStr = cursor.getString(widthIndex);
            String heigthStr = cursor.getString(heigthIndex);
            String sizeStr = cursor.getString(sizeIndex);
            String material = cursor.getString(materialIndex);
            long exeTime = cursor.getLong(timeIndex);

            int uploadFlag = cursor.getInt(uploadFlagIndex);


            StlGcode stlGcode = new StlGcode(id, sourceStlName, realStlName, sourceZipStlName,
                    serverZipGcodeName, localGcodeName, createTime, localImg,
                    lengthStr, widthStr, heigthStr, sizeStr, material, exeTime, IOUtil.getTimeStr(exeTime), uploadFlag, 0);
            stlDataBaseMap.put(stlGcode.getRealStlName(), stlGcode);
        }
        db.close();
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

        Cursor cursor = db.query(ThreePrinterEntry.TABLE_NAME, null, null, null, null, null, null);
        int idIndex = cursor.getColumnIndex(ThreePrinterEntry._ID);

        long id = 0;
        if (cursor.moveToNext()) {
            id = cursor.getInt(idIndex);
        }

        ContentValues values = new ContentValues();
        if (id == 0) {
            values.put(ThreePrinterEntry.COLUMN_WIFI_URL, url);
            id = db.insert(ThreePrinterEntry.TABLE_NAME, null, values);
            setEsp8266Url(url);
        } else {
            values.put(ThreePrinterEntry.COLUMN_WIFI_URL, url);
            String whereClause = ThreePrinterEntry._ID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(id)};
            db.update(ThreePrinterEntry.TABLE_NAME, values, whereClause, whereArgs);
            setEsp8266Url(url);
        }
        db.close();
        return id;

    }


    public static void getPrinterUrl(Context context) {
        SQLiteDatabase db = getDbByContext(context);
        Cursor cursor = db.query(ThreePrinterEntry.TABLE_NAME, null, null, null, null, null, null);
        int idIndex = cursor.getColumnIndex(ThreePrinterEntry._ID);
        int urlIndex = cursor.getColumnIndex(ThreePrinterEntry.COLUMN_WIFI_URL);

        if (cursor.moveToNext()) {
            int id = cursor.getInt(idIndex);
            String url = cursor.getString(urlIndex);
            setEsp8266Url(url);
        } else {
            setEsp8266Url(null);
        }
        db.close();
    }


    public static void updatePrinterUrl(Context context, String url) {
        SQLiteDatabase db = getDbByContext(context);
        if (url == null || url.length() == 0) {
            url = "";
        }
        ContentValues values = new ContentValues();
        values.put(ThreePrinterEntry.COLUMN_WIFI_URL, url);
        String whereClause = ThreePrinterEntry.COLUMN_WIFI_URL + " = ?";
        String[] whereArgs = new String[]{PrinterConfig.ESP_8266_URL};
        db.update(ThreePrinterEntry.TABLE_NAME, values, whereClause, whereArgs);
        setEsp8266Url(url);
        db.close();
    }


    public static List<StlGcode> getLocalStl() {

        if (localStlList == null || localStlList.size() == 0) {
            localMapStl.clear();
            localStlList = new ArrayList<>();
            /*StlGcode kitty = new StlGcode(1, "hello_kitty.stl",
                    "file:///android_asset/models/stl/localModules/hello_kitty.stl", "",
                    "",
                    "file:///android_asset/models/stl/localModules/hello_kitty.gco", "",
                    "file:///android_asset/models/stl/localModules/hello_kitty.png",
                    "X:74.01", "Y:51.22", "Z:100.93", "18.20M", "7318cm",
                    1025 * PrinterConfig.MINUTE_TIME, IOUtil.getTimeStr(1025 * PrinterConfig.MINUTE_TIME), 1, 1);
            localStlList.add(kitty);
            localMapStl.put(kitty.getLocalGcodeName().split("/localModules/")[1], kitty);
*/
            StlGcode chamaeleo_t = new StlGcode(2, "chamaeleo_t.stl",
                    "file:///android_asset/models/stl/localModules/chamaeleo_t.stl", "",
                    "",
                    "file:///android_asset/models/stl/localModules/chamaeleo_t.gco", "",
                    "file:///android_asset/models/stl/localModules/chamaeleo_t.png",
                    "X:92.89", "Y:93.08", "Z:25.98", "5.33M", "780cm",
                    110 * PrinterConfig.MINUTE_TIME, IOUtil.getTimeStr(110 * PrinterConfig.MINUTE_TIME), 1, 1);
            localStlList.add(chamaeleo_t);
            localMapStl.put(chamaeleo_t.getLocalGcodeName().split("/localModules/")[1], chamaeleo_t);

            StlGcode hand_ok = new StlGcode(3, "hand_ok.stl",
                    "file:///android_asset/models/stl/localModules/hand_ok.stl", "",
                    "",
                    "file:///android_asset/models/stl/localModules/hand_ok.gco", "",
                    "file:///android_asset/models/stl/localModules/hand_ok.png",
                    "X:42.78", "Y:57.72", "Z:110.44", "16.40M", "2168cm",
                    304 * PrinterConfig.MINUTE_TIME, IOUtil.getTimeStr(304 * PrinterConfig.MINUTE_TIME), 1, 1);
            localStlList.add(hand_ok);
            localMapStl.put(hand_ok.getLocalGcodeName().split("/localModules/")[1], hand_ok);

            StlGcode jet_pack_bunny = new StlGcode(4, "jet_pack_bunny.stl",
                    "file:///android_asset/models/stl/localModules/jet_pack_bunny.stl", "",
                    "",
                    "file:///android_asset/models/stl/localModules/jet_pack_bunny.gco", "",
                    "file:///android_asset/models/stl/localModules/jet_pack_bunny.png",
                    "X:130.43", "Y:92.01", "Z:131.28", "48.20M", "2168cm",
                    304 * PrinterConfig.MINUTE_TIME, IOUtil.getTimeStr(304 * PrinterConfig.MINUTE_TIME), 1, 1);
            localStlList.add(jet_pack_bunny);
            localMapStl.put(jet_pack_bunny.getLocalGcodeName().split("/localModules/")[1], jet_pack_bunny);

            StlGcode god_of_wealth = new StlGcode(5, "god_of_wealth.stl",
                    "file:///android_asset/models/stl/localModules/god_of_wealth.stl", "",
                    "",
                    "file:///android_asset/models/stl/localModules/god_of_wealth.gco", "",
                    "file:///android_asset/models/stl/localModules/god_of_wealth.png",
                    "X:62.85", "Y:57.72", "Z:64.23", "23.40M", "1945cm",
                    273 * PrinterConfig.MINUTE_TIME, IOUtil.getTimeStr(273 * PrinterConfig.MINUTE_TIME), 1, 1);
            localStlList.add(god_of_wealth);
            localMapStl.put(god_of_wealth.getLocalGcodeName().split("/localModules/")[1], god_of_wealth);
        }
        return localStlList.size() == 0 ? null : localStlList;
    }


    /**
     * 设置打印机上传数据
     */
    public static synchronized void setPrinterUploadInfo(StlGcode stlGcode, int isUpload, int printType) {
        Map<String, StlGcode> map = new HashMap<>();
        for (PrinterGcodeInfo e : PrinterConfig.printerList) {
            map.put(e.getStlGcode().getShortGcode(), e.getStlGcode());
        }
        if (!map.containsKey(stlGcode.getShortGcode())) {
            PrinterGcodeInfo printerGcodeInfo = new PrinterGcodeInfo();

            printerGcodeInfo.setStlGcode(stlGcode);
            printerGcodeInfo.setBegin_time(System.currentTimeMillis());
            printerGcodeInfo.setIs_upload(isUpload);
            printerGcodeInfo.setPrinter_type(printType);

            PrinterConfig.printerList.add(printerGcodeInfo);

            if (PrinterConfig.currPrinterGcodeInfo == null) {
                PrinterConfig.currPrinterGcodeInfo = printerGcodeInfo;
            }
            if (isUpload > 0) {
                PrinterConfig.printer_count++;
            } else {
                PrinterConfig.upload_count++;
            }
        }
        map.clear();
    }


    public static synchronized void deletePrinterInfo(StlGcode stlGcode) {

    }


    private static synchronized void setEsp8266Url(String url) {
        if (url == null || url.length() == 0) {
            PrinterConfig.ESP_8266_URL = null;
            PrinterConfig.ESP_WEB_SOCKET = null;
        } else {
            PrinterConfig.ESP_8266_URL = url;
            String ws = url.replace("http", "ws");
            if (ws.endsWith("/")) {
                PrinterConfig.ESP_WEB_SOCKET = ws.substring(0, ws.length() - 1) + ":81/";
            } else {
                PrinterConfig.ESP_WEB_SOCKET = ws + ":81/";
            }
        }
    }

    public static synchronized void resetEsp8266Url() {
        PrinterConfig.ESP_8266_URL = null;
        PrinterConfig.ESP_WEB_SOCKET = null;

    }
}
