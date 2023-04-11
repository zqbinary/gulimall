package com.atguigu.gulimall.product.web;


import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class IndexController {
    @Resource
    private CategoryService categoryService;

    @GetMapping("/")
    public R indexPage() {
        //1、查出所有的一级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categories();
        return R.ok().setData(categoryEntities);
    }

    @GetMapping("ping")
    public String ping() {
        return "pong";
    }


    @GetMapping(value = "/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        return categoryService.getCatalogJson();
    }

    @Autowired
    RedissonClient redissonClient;
    String uuid = "init";


    @GetMapping("/write")
    public String write() {
        RReadWriteLock rw = redissonClient.getReadWriteLock("rw");
        RLock rLock = rw.writeLock();
        rLock.lock();
        try {
            uuid = UUID.randomUUID().toString();
            Thread.sleep(10000);
        } catch (Exception e) {

        } finally {
            rLock.unlock();
        }
        return "write uuid:" + uuid;
    }


    @GetMapping("/read")
    public String read() {
        RReadWriteLock rw = redissonClient.getReadWriteLock("rw");
        RLock rLock = rw.readLock();
        rLock.lock();
        try {
            return "read:" + uuid;
        } finally {
            rLock.unlock();
        }
    }
}
