package com.atguigu.gulimall.product.web;


import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catalogs2Vo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    public Map<String, List<Catalogs2Vo>> getCatalogJson() {
        return categoryService.getCatalogJson();
    }

}
