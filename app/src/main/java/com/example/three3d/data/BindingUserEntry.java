package com.example.three3d.data;

import android.provider.BaseColumns;

/**
 * Inner class that defines constant values for the pets database table.
 * Each entry in the table represents a single pet.
 */
public final class BindingUserEntry implements BaseColumns {

    /** Name of database table for pets */
    public final static String TABLE_NAME = "binding_user";
//    public final static String _ID = BaseColumns._ID;

    public final static String COLUMN_USER_ID ="user_id";
    // 关联userid
    public final static String COLUMN_BINDING_USERID ="binding_userid";
    // 创建时间
    public final static String COLUMN_CREATE_TIME ="create_time";
    // 用来标识记录的状态 0本地新增,-1标记删除,1本地更新
    public final static String COLUMN_STATUS ="status";
    //anchor_time ： 记录服务端同步过来的时间戳
    public final static String COLUMN_ANCHOR_TIME ="anchor_time";

}