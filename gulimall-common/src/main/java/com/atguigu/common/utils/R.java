package com.atguigu.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.exception.BizCodeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年10月27日 下午9:59:27
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public R() {
        put("code", 0);
    }

    public static R error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(500, msg);
    }

    public static R error(BizCodeEnum bizCodeEnum) {
        return error(bizCodeEnum.getCode(), bizCodeEnum.getMsg());
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        return new R();
    }

    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }


    //todo nullalble 不太好吧
    public Integer getCode() {
        return (Integer) this.get("code");
    }

    public R setData(Object data) {
        put("data", data);
        return this;
    }

    // 利用fastjson进行反序列化
    public <T> T getData(TypeReference<T> typeReference) {
        // 默认是map
        Object data = get("data");
        String jsonString = JSON.toJSONString(data);
        return JSON.parseObject(jsonString, typeReference);
    }

    public <T> T getData(String key, TypeReference<T> typeReference) {
        // 默认是map
        Object data = get(key);
        String jsonString = JSON.toJSONString(data);
        return JSON.parseObject(jsonString, typeReference);
    }

}
