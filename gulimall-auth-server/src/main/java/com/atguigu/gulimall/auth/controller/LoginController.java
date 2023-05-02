package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.gulimall.auth.feign.ThirdPartyFeignService;
import com.atguigu.gulimall.auth.vo.UserRegisterVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class LoginController {
    @Autowired
    ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MemberFeignService memberFeignService;

    @ResponseBody
    @GetMapping(value = "/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {

        String key = AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone;
        String codeRedis = stringRedisTemplate.opsForValue().get(key);
        //1、接口防刷
        if (!StringUtils.isEmpty(codeRedis)) {
            String[] split = codeRedis.split("_");
            //活动存入redis的时间，用当前时间减去存入redis的时间，判断用户手机号是否在60s内发送验证码
            long sendTime = Long.parseLong(split[1]);
            //60s内不能再发
            if (System.currentTimeMillis() - sendTime < 160000) {
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION);
            }
            //存入redis，防止同一个手机号在60秒内再次发送验证码

        }
        //2、验证码的再次效验 redis.存key-phone,value-code
        String code = String.format("%06d", (int) (Math.random() * 1000000));
        stringRedisTemplate.opsForValue().set(key, code + "_" + System.currentTimeMillis(), 10, TimeUnit.MINUTES);
        log.info("auth server,code is :{}", code);
        thirdPartyFeignService.sendCode(phone, code);

        //code debug 用
        return R.ok(code);
    }


    /**
     * TODO: 重定向携带数据：利用session原理，将数据放在session中。
     * TODO:只要跳转到下一个页面取出这个数据以后，session里面的数据就会删掉
     * TODO：分布下session问题
     * RedirectAttributes：重定向也可以保留数据，不会丢失
     * 用户注册
     *
     * @return
     */
    @PostMapping("/register")
    public String register(@Valid UserRegisterVo vos, BindingResult result, RedirectAttributes attributes) {
        //如果有错误回到注册页面
        //效验出错回到注册页面
        HashMap<String, String> errors = new HashMap<>();
        if (result.hasErrors()) {
            errors = (HashMap<String, String>) result.getFieldErrors().stream()
                    // password有两个验证，有duplicate key 问题
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (v1, v2) -> v1));
            return errPage(attributes, errors);
        }
        //1、效验验证码
        String code = vos.getCode();
        //获取存入Redis里的验证码

        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vos.getPhone());
        //截取字符串
        if (StringUtils.isEmpty(redisCode)) {
            //效验出错回到注册页面
            errors.put("code", "验证码空");
            return errPage(attributes, errors);
        }
        String codeStore = redisCode.split("_")[0];
        if (!StringUtils.equals(vos.getCode(), codeStore)) {
            errors.put("code", "验证码错误");
            return errPage(attributes, errors);
        }

        //删除验证码;令牌机制
        stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vos.getPhone());
        //验证码通过，真正注册，调用远程服务进行注册
        R register = memberFeignService.register(vos);
        if (0 != register.getCode()) {
            errors.put("msg", register.getData("msg", new TypeReference<String>() {
            }));
            return errPage(attributes, errors);
        }
        return "redirect:http://auth.gulimall.com/login.html";
    }

    private String errPage(RedirectAttributes attributes, Map<String, String> errorMap) {

        log.info("errors{}", errorMap);
        attributes.addFlashAttribute("errors", errorMap);
        return "redirect:http://auth.gulimall.com/reg.html";

    }
    //从session先取出来用户的信息，判断用户是否已经登录过了
    //如果用户没登录那就跳转到登录页面
//gulimall.com";
    //远程登录
//gulimall.com";
//auth.gulimall.com/login.html";
//gulimall.com";
}
