package com.atguigu.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description: 封装页面所有可能传递过来的查询条件
 * @Created: with IntelliJ IDEA.
 * @author: 夏沫止水
 * @createTime: 2020-06-13 14:17
 **/

@Data
public class SearchParam {

    /**
     * 页面传递过来的全文匹配关键字
     */
    private String keyword;

    /**
     * 品牌id,可以多选
     * 前端是 brandId=x1&brandId=x2
     */
    private List<Long> brandId;

    /**
     * 三级分类id
     */
    private Long catalog3Id;

    /**
     * 排序条件：sort=price/salecount/hotscore_desc/asc
     */
    private String sort;

    /**
     * 是否显示有货
     */
    private Integer hasStock;

    /**
     * 价格区间查询
     * 约定 100_500,_500,100_
     */
    private String skuPrice;

    /**
     * 按照属性进行筛选
     * attrs=1_其他:安卓：鸿蒙&attrs=2_屏幕:6寸
     * 属性id_属性名：属性值1：属性值2
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 原生的所有查询条件
     */
    private String _queryString;


}
