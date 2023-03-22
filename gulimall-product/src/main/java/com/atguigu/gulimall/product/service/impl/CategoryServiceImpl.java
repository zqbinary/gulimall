package com.atguigu.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree(
            Map<String, Object> params) {
        //todo all
        List<CategoryEntity> all = list();

        List<CategoryEntity> collect = all.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                //todo map peek 返回值？
                .peek(categoryEntity -> {
                    categoryEntity.setChildren(this.getChildrens(categoryEntity, all));
                })
                //1 todo 封装
                // sorted 里面是 Comparator
                .sorted(Comparator.comparingInt(a -> (a.getSort() == null ? 0 : a.getSort())))
                .collect(Collectors.toList());
        return collect;

    }

    @Override
    public void removeMenuByIds(List<Long> ids) {
        //1 判断引用

        //改逻辑

        removeByIds(ids);
    }

    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        return all.stream()
                .filter(categoryEntity -> Objects.equals(categoryEntity.getParentCid(), root.getCatId()))
                .peek(categoryEntity -> {
                    //1、找到子菜单
                    categoryEntity.setChildren(getChildrens(categoryEntity, all));
                })
                .sorted((a, b) -> (a.getSort() == null ? 0 : a.getSort()) - (b.getSort() == null ? 0 : b.getSort()))
                .collect(Collectors.toList());
    }

//    private Comparator<CategoryEntity> sortItem(CategoryEntity a) {
//        return a.getSort() == null ? 0 : a.getSort();
//    }
}
