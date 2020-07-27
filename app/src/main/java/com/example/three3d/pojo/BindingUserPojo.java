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
    // 用来标识记录的状态 0本地新增,-1标记删除,1本地更新
    private String status;
    // anchor_time ： 记录服务端同步过来的时间戳
    private String anchor_time;


}
