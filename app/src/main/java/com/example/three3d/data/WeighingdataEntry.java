package com.example.three3d.data;

import android.provider.BaseColumns;

/**
 * Inner class that defines constant values for the pets database table.
 * Each entry in the table represents a single pet.
 */
public final class WeighingdataEntry implements BaseColumns {

    /** Name of database table for pets */
    public final static String TABLE_NAME = "weighing_data";
    //自增涨id
    public final static String _ID = BaseColumns._ID;
    // 用户id
    public final static String COLUMN_USER_ID ="user_id";
    // 关联用户
    public final static String COLUMN_UUID ="uuid";
    //类别（盐，油）
    public final static String COLUMN_ITEM ="item";
    // 类型 (手动输入，设备数据)
    public final static String COLUMN_TYPE="type";
    // 称重数
    public final static String COLUMN_WEIGHT ="weight";
    // 创建时间
    public final static String COLUMN_CREATE_TIME ="create_time";
    // 浪费比率
    public final static String COLUMN_WASTE_RATE ="waste_rate";
    // 进餐人数
    public final static String COLUMN_NUMBER ="number";
    // 更新时间
    public final static String COLUMN_MODIFY_TIME ="modify_time";
    // 删除状态
    public final static String COLUMN_STATUS ="status";
}