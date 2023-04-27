package com.atguigu.gulimall.auth.controller;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.ThirdPartyFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class LoginController {
    @Autowired
    ThirdPartyFeignService thirdPartyFeignService;

    @GetMapping("login.html")
    public String loginPage() {
        return "login";
    }

    @GetMapping("reg.html")
    public String regPage() {
        return "reg";
    }

    @ResponseBody
    @GetMapping(value = "/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {
//        String substring = UUID.randomUUID().toString().substring(0, 6);
//        int code = (Math.random()*9+1) * 1000000;
        int code = 123456;
        log.info("auth server:{}", code);
        thirdPartyFeignService.sendCode(phone, String.valueOf(code));
        return R.ok();
    }
}
