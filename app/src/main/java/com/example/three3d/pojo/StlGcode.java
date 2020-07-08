package com.example.three3d.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StlGcode {

    private int id;

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
    private String material;

}
