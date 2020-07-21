package com.kairong.three3d.pojo;

import android.text.TextUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StlGcode {

    private long id;

    // stl原始文件名称
    private String sourceStlName;

    // 真实文件名称
    private String realStlName;

    // stl压缩文件名称
    private String sourceZipStlName;

    // 服务器返回文件
    private String serverZipGcodeName;

    // 本地解压文件
    private String localGcodeName;

    private String createTime;

    // 本地缩略图
    private String localImg;

    // stl 长宽高 大小
    private String length;
    private String width;
    private String height;
    private String size;

    // 材料长度
    private String material;

    // 执行打印时间
    private long exeTime;

    private String exeTimeStr;

    // 是否上传打印机 0未上传 1上传
    private int flag;

    // 是否本地文件  1本地文件 0创建文件
    private int localFlag;


    public String getShortGcode() {
        if (TextUtils.isEmpty(localGcodeName)) {
            return null;
        }
        return localGcodeName.substring(localGcodeName.lastIndexOf("/") + 1);
    }

}
