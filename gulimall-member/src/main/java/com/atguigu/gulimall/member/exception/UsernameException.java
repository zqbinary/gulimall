package com.atguigu.gulimall.member.exception;

public class UsernameException extends RuntimeException {
    public UsernameException() {
        super("username 已存在");
    }
}
