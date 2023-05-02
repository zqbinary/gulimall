package com.atguigu.gulimall.member.vo;

import lombok.Data;


@Data
public class MemberUserRegisterVo {
    //微服务过来的，不再验证
    private String userName;

    private String password;

    private String phone;
}
