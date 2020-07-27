package com.example.three3d.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentPojo {

    private int id;

    // mac地址
    private String mac;
    // 设备编号
    private String uuId;
    // 名字
    private String name;
    // 用户id
    private String userId;
    // 绑定内容(盐、糖或其他自定义)
    private String item;
    // 称重单位
    private String unit;
    // 推荐指标
    private String target;
    // 创建时间
    private String createTime;
    // 更新时间
    private String updateTime;
    // ip地址
    private String ipAddress;
    // 1：蓝牙，2：wifi
    private String onlineType;

}
