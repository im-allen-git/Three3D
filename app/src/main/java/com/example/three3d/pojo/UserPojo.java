package com.example.three3d.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPojo {

    private int id;

    // 用户id
    private String userId;
    // 昵称(默认手机号，第三方登录使用第三方昵称)
    private String nickName;
    // 密码
    private String password;
    // 手机
    private String mobile;
    // 性别 1:男，2：女
    private String sex;
    // 出生日期
    private String birthday;
    // 身高
    private String height;
    // 体重
    private String weight;
    // 浪费率
    private String wasteRate;
    // 用餐人数
    private String number;
    // 创建时间
    private String createTime;

}
