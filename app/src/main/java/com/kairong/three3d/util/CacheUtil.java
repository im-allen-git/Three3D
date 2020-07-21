package com.kairong.three3d.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.kairong.three3d.pojo.StlGcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CacheUtil {

    private static final String TAG = CacheUtil.class.getSimpleName();

    public static volatile List<StlGcode> sdList = new ArrayList<>();
    public static volatile Map<String, StlGcode> sdMap = new HashMap<>();

    /**
     * 获取SD卡list数据
     *
     * @param flag
     * @return
     */
    public static List<StlGcode> getSdList(int flag) {
        if (flag > 0) {
            getSdListByOkHttp();
        } else {
            if (sdList.size() == 0) {
                getSdListByOkHttp();
            }
        }
        return sdList;
    }


    private static void getSdListByOkHttp() {
        OkHttpClient client = OkHttpUtil.getClient();
        Request request = OkHttpUtil.getRequest(PrinterUtil.getSDListUrl());
        try {
            Response execute = client.newCall(request).execute();
            if (execute.isSuccessful()) {
                String rs = execute.body().string();
                if (!TextUtils.isEmpty(rs)) {
                    sdList.clear();
                    sdMap.clear();
                    String[] childList = null;
                    String[] rsList = rs.split("\n");
                    for (int i = 0; i < rsList.length; i++) {
                        if (rsList[i].contains("file list")) {
                            continue;
                        }
                        childList = rsList[i].trim().split(" ");
                        StlGcode stlGcode = new StlGcode();
                        stlGcode.setLocalGcodeName(childList[0]);
                        int fileS = Integer.parseInt(childList[1].trim());
                        IOUtil.genFileSize(fileS, stlGcode);
                        sdList.add(stlGcode);
                        sdMap.put(stlGcode.getLocalGcodeName(), stlGcode);
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 将字符串数据保存到本地
     *
     * @param context  上下文
     * @param filename 生成XML的文件名
     * @param map      map<生成XML中每条数据名,需要保存的数据>
     */
    public static void saveSettingNote(Context context, String filename, Map<String, String> map) {
        SharedPreferences.Editor note = context.getSharedPreferences(filename, Context.MODE_PRIVATE).edit();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
            note.putString(entry.getKey(), entry.getValue());
        }
        note.apply();
    }

    /**
     * 从本地取出要保存的数据
     *
     * @param context  上下文
     * @param filename 文件名
     * @param dataname 生成XML中每条数据名
     * @return 对应的数据(找不到为NUll)
     */
    public static String getSettingNote(Context context, String filename, String dataname) {
        SharedPreferences read = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return read.getString(dataname, null);
    }
}
