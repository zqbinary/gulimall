package com.atguigu.gulimall.auth.vo;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UserRegisterVo {
    @NotBlank(message = "请输入用户名")
    @Length(min = 6, max = 20, message = "长度6-20")
    private String userName;

    @NotBlank(message = "password不能空")
    @Length(min = 6, message = "密码6-20")
    private String password;

    @NotBlank(message = "phone 不能空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    @NotEmpty(message = "code 不能空")
    private String code;

}
