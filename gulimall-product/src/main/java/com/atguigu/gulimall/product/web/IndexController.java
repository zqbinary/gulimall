package com.atguigu.gulimall.product.web;


import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
public class IndexController {
    @Resource
    private CategoryService categoryService;
    @Autowired
    RedissonClient redissonClient;
    String uuid = "init";

    @GetMapping("/")
    private String indexPage(Model model, HttpSession session) {

        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        log.info("seesion:{}", attribute);

        //1、查出所有的一级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();
        model.addAttribute("categories", categoryEntities);

        return "index";
    }


    @GetMapping("ping")
    @ResponseBody
    public String ping() {
        return "pong";
    }


    @GetMapping(value = "/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        return categoryService.getCatalogJson();
    }


    @GetMapping("/write")
    @ResponseBody
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
