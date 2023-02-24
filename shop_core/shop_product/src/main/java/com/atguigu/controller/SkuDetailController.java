package com.atguigu.controller;


import com.atguigu.entity.BaseCategoryView;
import com.atguigu.entity.ProductSalePropertyKey;
import com.atguigu.entity.SkuInfo;
import com.atguigu.service.BaseCategoryViewService;
import com.atguigu.service.SkuDetailService;
import com.atguigu.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 一级分类表 前端控制器
 * </p>
 *
 * @author panajanemo
 * @since 2023-02-23
 */
@RestController
@RequestMapping("/sku")
public class SkuDetailController {

    @Autowired
    private SkuDetailService skuDetailService;
    @Autowired
    private BaseCategoryViewService categoryViewService;
    @Autowired
    private SkuInfoService skuInfoService;

    //1.根据商品skuId查询商品的基本信息
    @GetMapping("getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId){
        return skuDetailService.getSkuInfo(skuId);
    }
    //2.根据三级分类id查询商品的分类信息 //select * from base_category_view a where a.category3_id=11
    @GetMapping("getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable Long category3Id){
        return categoryViewService.getById(category3Id);
    }
    //3.根据skuId查询商品的实时价格
    @GetMapping("getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable Long skuId){
        SkuInfo skuInfo = skuInfoService.getById(skuId);
        return skuInfo.getPrice();
    }
    //4.销售属性组合与skuId的对应关系
    @GetMapping("getSalePropertyAndSkuIdMapping/{productId}")
    public Map<Object,Object> getSalePropertyAndSkuIdMapping(@PathVariable Long productId){
        return skuDetailService.getSalePropertyAndSkuIdMapping(productId);
    }
    //5.该sku对应的销售属性(一份)所有的销售属性(全份)
    @GetMapping("getSpuSalePropertyAndSelected/{productId}/{skuId}")
    public List<ProductSalePropertyKey> getSpuSalePropertyAndSelected(@PathVariable Long productId, @PathVariable Long skuId){
        return skuDetailService.getSpuSalePropertyAndSelected(productId,skuId);
    }
}

