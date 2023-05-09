package com.atguigu.gulimall.order.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WmsFeignService {
    @PostMapping(value = "/ware/waresku/hasStock")
    R getSkuHasStock(List<Long> skuIds);
}
