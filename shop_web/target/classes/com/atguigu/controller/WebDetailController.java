package com.atguigu.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.ProductFeignClient;
import com.atguigu.entity.BaseCategoryView;
import com.atguigu.entity.ProductSalePropertyKey;
import com.atguigu.entity.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WebDetailController {
    @Autowired
    private ProductFeignClient productFeignClient;

    @RequestMapping("{skuId}.html")
    public String getSkuDetail(@PathVariable Long skuId, Model model){
        Map<String, Object> dataMap = new HashMap<>();
        //1.根据商品skuId查询商品的基本信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        dataMap.put("skuInfo",skuInfo);

        //2.根据三级分类id查询商品的分类信息
        Long category3Id = skuInfo.getCategory3Id();
        BaseCategoryView categoryView = productFeignClient.getCategoryView(category3Id);
        dataMap.put("categoryView",categoryView);

        //3.根据skuId查询商品的实时价格
        BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
        dataMap.put("price",skuPrice);

        //4.销售属性组合与skuId的对应关系
        Long productId = skuInfo.getProductId();
        Map<Object, Object> salePropertyAndSkuIdMapping = productFeignClient.getSalePropertyAndSkuIdMapping(productId);
        dataMap.put("salePropertyValueIdJson", JSON.toJSONString(salePropertyAndSkuIdMapping));

        //5.该sku对应的销售属性(一份)所有的销售属性(全份)
        List<ProductSalePropertyKey> spuSalePropertyList = productFeignClient.getSpuSalePropertyAndSelected(productId, skuId);
        dataMap.put("spuSalePropertyList",spuSalePropertyList);

        model.addAllAttributes(dataMap);
        return "detail/index";
    }
}
