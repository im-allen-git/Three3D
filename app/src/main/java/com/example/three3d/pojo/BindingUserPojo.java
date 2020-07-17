package com.example.three3d.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BindingUserPojo {

    private int id;

    // 用户id
    private String userId;
    // 绑定userid
    private String bindingUserid;
    // 创建时间
    private String createTime;

}
