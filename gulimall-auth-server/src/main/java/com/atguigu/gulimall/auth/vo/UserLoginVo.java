package com.atguigu.gulimall.auth.vo;

import lombok.Data;

@Data
public class UserLoginVo {

    //todo 校验
    private String loginacct;

    //todo 校验
    private String password;
}
