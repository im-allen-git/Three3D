package com.kairong.three3d.pojo;

import lombok.Data;

@Data
public class PrinterGcodeInfo {


    private StlGcode stlGcode;

    /**
     * 上传或者打印开始时间
     */
    private long begin_time;
    /**
     * 上传打印类型 0创建模型上传打印  1打印存在模型
     */
    private int printer_type = -1;

    /**
     * 上传打印标识  0上传 1打印
     */
    private int is_upload = -1;

}
