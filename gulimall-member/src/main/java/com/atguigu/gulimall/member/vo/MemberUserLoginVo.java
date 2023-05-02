package com.atguigu.gulimall.member.vo;

import lombok.Data;

@Data
public class MemberUserLoginVo {
    //todo 校验
    private String loginacct;

    //todo 校验
    private String password;
}
