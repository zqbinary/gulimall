package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.to.vo.SkuHasStockVo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.dao.SpuInfoDao;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SearchFeignService serviceFeignService;
    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService attrValueService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        //状态修改
        //todo 这个要解耦吗
        SpuInfoEntity spu = getById(spuId);

        List<SkuEsModel> skuEsModels = genEsList(spuId, spu);

        //发送到es, search
        R r = serviceFeignService.productStatusUp(skuEsModels);
        //todo 超时
        if (r.getCode() == 0) {
            this.baseMapper.updateSpuStatus(spuId, ProductConstant.ProductStatusEnum.SPU_UP.getCode());
        }
    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {
        //先查询sku表里的数据
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);

        //获得spuId
        Long spuId = skuInfoEntity.getSpuId();

        //再通过spuId查询spuInfo信息表里的数据
        SpuInfoEntity spuInfoEntity = this.baseMapper.selectById(spuId);

        //查询品牌表的数据获取品牌名
        BrandEntity brandEntity = brandService.getById(spuInfoEntity.getBrandId());
        spuInfoEntity.setBrandName(brandEntity.getName());

        return spuInfoEntity;
    }

    private Map<Long, Boolean> getStockMap(List<Long> skuIdList) {
        try {

            R skuHasStock = wareFeignService.getSkuHasStock(skuIdList);
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {
            };
            return skuHasStock.getData(typeReference).stream()
                    .collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
        } catch (Exception e) {
            log.error("库存查询异常,原因：{}", e);
            return null;
        }

    }

    private List<SkuEsModel> genEsList(Long spuId, SpuInfoEntity spu) {
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIdList = skuInfoEntities.stream().map(sku -> sku.getSkuId()).collect(Collectors.toList());
        Map<Long, Boolean> stockMap = getStockMap(skuIdList);

        BrandEntity brand = brandService.getById(spu.getBrandId());
        CategoryEntity category = categoryService.getById(spu.getCatalogId());


        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrListforspu(spuId);

        //这些spu有这些 attrId
        List<Long> attrIds = baseAttrs.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());

        List<Long> searchAttrIds = attrService.selectSearchAttrs(attrIds);
        //转换为Set集合
        Set<Long> idSet = new HashSet<>(searchAttrIds);

        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream()
                .filter(item -> idSet.contains(item.getAttrId()))
                .map(item -> {
                    SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(item, attrs);
                    return attrs;
                }).collect(Collectors.toList());
        List<SkuEsModel> collect = skuInfoEntities.stream()
                .map(sku -> {
                    SkuEsModel esModel = new SkuEsModel();
                    esModel.setSkuPrice(sku.getPrice())
                            .setSkuImg(sku.getSkuDefaultImg());
                    //库存
                    if (stockMap != null && stockMap.containsKey(sku.getSkuId())) {
                        esModel.setHasStock(stockMap.get(sku.getSkuId()));
                    } else {
                        //逻辑上是，库存没有就算了
                        esModel.setHasStock(true);
                    }
                    // attr
                    esModel.setAttrs(attrsList);
                    //热度评分
                    esModel.setHotScore(0L);
                    //品牌
                    esModel.setBrandId(brand.getBrandId())
                            .setBrandName(brand.getName())
                            .setBrandImg(brand.getLogo());

                    //分类
                    esModel.setCatalogId(category.getCatId())
                            .setCatalogName(category.getName());
                    //其他
                    BeanUtils.copyProperties(sku, esModel);
                    return esModel;
                }).collect(Collectors.toList());
        return collect;
    }


}