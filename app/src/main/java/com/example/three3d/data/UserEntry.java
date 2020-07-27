package com.example.three3d.data;

import android.provider.BaseColumns;

/**
 * Inner class that defines constant values for the pets database table.
 * Each entry in the table represents a single pet.
 */
public final class UserEntry implements BaseColumns {

    /** Name of database table for pets */
    public final static String TABLE_NAME = "user_saz";
    public final static String COLUMN_USER_ID = BaseColumns._ID;

//    public final static String COLUMN_USER_ID ="user_id";
    // 昵称(默认手机号，第三方登录使用第三方昵称)
    public final static String COLUMN_NICK_NAME ="nick_name";
//    // 密码
//    public final static String COLUMN_PASSWORD ="password";
    // 手机
    public final static String COLUMN_MOBILE ="mobile";
    // 性别 1:男，2：女
    public final static String COLUMN_SEX ="sex";
    // 出生日期
    public final static String COLUMN_BIRTHDAY ="birthday";
    // 身高
    public final static String COLUMN_HEIGHT ="height";
    // 体重
    public final static String COLUMN_WEIGHT ="weight";
    // 浪费率
    public final static String COLUMN_WASTE_RATE ="waste_rate";
    // 用餐人数
    public final static String COLUMN_NUMBER ="number";
    // 创建时间
    public final static String COLUMN_CREATE_TIME ="create_time";
    // 用来标识记录的状态 0本地新增,-1标记删除,1本地更新
    public final static String COLUMN_STATUS ="status";
    //anchor_time ： 记录服务端同步过来的时间戳
    public final static String COLUMN_ANCHOR_TIME ="anchor_time";


}