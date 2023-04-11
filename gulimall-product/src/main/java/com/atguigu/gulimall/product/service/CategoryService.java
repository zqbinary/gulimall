package com.atguigu.gulimall.product.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2023-03-17 11:20:49
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree(Map<String, Object> params);

    void removeMenuByIds(List<Long> asList);

    // 查找一级分类，首页显示
    List<CategoryEntity> getLevel1Categories();

    /**
     * 查找二级、三级分类，首页显示
     *
     * @return
     */

    public void updateCascade(CategoryEntity category);

    Map<String, List<Catelog2Vo>> getCatalogJson();


    List<CategoryEntity> getLevel1Categorys();

//    Map<String, List<Catelog2Vo>> getCatalogJson();
}

