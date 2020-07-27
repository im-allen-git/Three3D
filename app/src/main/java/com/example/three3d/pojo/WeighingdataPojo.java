package com.example.three3d.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeighingdataPojo {

    private int id;

    // 用户id
    private String userId;
    // mac
    private String mac;
    // 类别（盐，油）
    private String item;
    // 类型 (手动输入，设备数据)
    private String type;
    // 称重数
    private String weight;
    // 创建时间
    private String createTime;
    // 浪费比率
    private String wasteRate;
    // 进餐人数
    private String number;
    // 更新时间
    private String modifyTime;
    // 删除状态
    private String status;
    // 重量集合数据（id:weight）
    private String weightStr;
    // id集合数据（id:id）
    private String idAllStr;
}
