package com.atguigu.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class RedissonConfig {
    /**
     * 所有对 Redisson 的使用都是通过 RedissonClient
     *
     * @return
     * @throws IOException
     */
    //todo
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson(@Value("${spring.redis.host}") String host, @Value("${spring.redis.port}") String port, @Value("${spring.redis.password}") String password) throws IOException {
        // 1、创建配置
        Config config = new Config();
        // Redis url should start with redis:// or rediss://
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password)
        ;

        // 2、根据 Config 创建出 RedissonClient 实例
        return Redisson.create(config);
    }
}
