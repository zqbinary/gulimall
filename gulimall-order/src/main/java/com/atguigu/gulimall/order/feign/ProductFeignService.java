package com.atguigu.gulimall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("gulimall-product")
public interface ProductFeignService {
}
