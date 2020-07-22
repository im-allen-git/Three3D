package com.kairong.three3d.config;

import com.kairong.three3d.pojo.PrinterGcodeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 打印相关参数
 */
public class PrinterConfig {


    /**
     * 打印机WIFI连接
     */
    public static volatile String ESP_8266_URL = null;

    /**
     * 打印机WIFI连接套接口
     */
    public static volatile String ESP_WEB_SOCKET = null;



    /**
     * 小时的微秒换算
     */
    public static final long HOUR_TIME = 60 * 60 * 1000;

    /**
     * 分钟的微秒换算
     */
    public static final long MINUTE_TIME = 60 * 1000;

    /**
     * 秒的微秒换算
     */
    public static final long SECOND_TIME = 1000;


    /**
     * 需要打印的数量
     */
    public static volatile int printer_count = 0;

    /**
     * 上传数量
     */
    public static volatile int upload_count = 0;


    public static volatile String printer_gcode = null;

    public static volatile List<PrinterGcodeInfo> printerList = new ArrayList<>();

    public static volatile PrinterGcodeInfo currPrinterGcodeInfo = null;

    public static volatile int is_background = 0;


}
