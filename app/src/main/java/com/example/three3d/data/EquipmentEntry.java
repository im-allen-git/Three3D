package com.example.three3d.data;

import android.provider.BaseColumns;

/**
 * Inner class that defines constant values for the pets database table.
 * Each entry in the table represents a single pet.
 */
public final class EquipmentEntry implements BaseColumns {

    /** Name of database table for pets */
    public final static String TABLE_NAME = "equipment";
    public final static String _ID = BaseColumns._ID;

    // mac地址
    public final static String COLUMN_MAC ="mac";
    // 设备编号
    public final static String COLUMN_UUID ="uuid";
    // 名字
    public final static String COLUMN_NAME ="name";
    // 用户id
    public final static String COLUMN_USER_ID ="user_id";
    // 绑定内容(盐、糖或其他自定义)
    public final static String COLUMN_ITEM ="item";
    // 称重单位
    public final static String COLUMN_UNIT="unit";
    // 推荐指标
    public final static String COLUMN_TARGET ="target";
    // 创建时间
    public final static String COLUMN_CREATE_TIME ="create_time";
    // 更新时间
    public final static String COLUMN_UPDATE_TIME ="update_time";
    // ip地址
    public final static String COLUMN_IP_ADDRESS ="ip_address";
    // 1：蓝牙，2：wifi
    public final static String COLUMN_ONLINE_TYPE ="online_type";

}