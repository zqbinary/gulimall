package com.atguigu.gulimall.member.exception;

public class PhoneException extends RuntimeException {
    public PhoneException() {
        super("存在相同手机号");
    }
}
