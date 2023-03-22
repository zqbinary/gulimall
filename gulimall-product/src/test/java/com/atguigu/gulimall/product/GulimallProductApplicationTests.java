package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setDescript("测试华为");
        brandEntity.setName("华为");
        boolean save = brandService.save(brandEntity);
        System.out.println("保养成功");
    }

}
