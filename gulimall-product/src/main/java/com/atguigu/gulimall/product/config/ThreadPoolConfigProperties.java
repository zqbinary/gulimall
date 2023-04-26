package com.atguigu.gulimall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("gulimall.thread")
@Data
@Component
public class ThreadPoolConfigProperties {

    Integer coreSize;
    Integer maxSize;
    Integer keepAliveTime;

}
