package com.atguigu.service.impl;

import com.atguigu.entity.ProductSalePropertyKey;
import com.atguigu.entity.SkuImage;
import com.atguigu.entity.SkuInfo;
import com.atguigu.mapper.ProductSalePropertyKeyMapper;
import com.atguigu.mapper.SkuSalePropertyValueMapper;
import com.atguigu.service.SkuDetailService;
import com.atguigu.service.SkuImageService;
import com.atguigu.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author panajanemo
 * @version 1.0.0
 * @title SkuDetailServiceImpl
 * @description TODO
 * @create 2023/2/23 18:05
 */
@Service
public class
SkuDetailServiceImpl implements SkuDetailService {
    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImageService skuImageService;

    @Autowired
    private SkuSalePropertyValueMapper salePropertyValueMapper;

    @Autowired
    private ProductSalePropertyKeyMapper salePropertyKeyMapper;

    /**
     * @param skuId
     * @return SkuInfo
     * @description 根据商品skuId查询商品的基本信息
     * @author panajanemo
     * @time 2023/2/24 15:29
     */
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        //1.查询商品基本信息
        SkuInfo skuInfo = skuInfoService.getById(skuId);
        //2.查询商品图片信息
        if (skuInfo != null) {
            QueryWrapper<SkuImage> wrapper = new QueryWrapper<>();
            wrapper.eq("sku_id", skuId);
            List<SkuImage> skuImageList = skuImageService.list(wrapper);
            skuInfo.setSkuImageList(skuImageList);
        }
        return skuInfo;
    }

    /**
     * @param productId
     * @return Map<Object>
     * @description 销售属性组合与skuId的对应关系
     * @author panajanemo
     * @time 2023/2/24 15:27
     */
    @Override
    public Map<Object, Object> getSalePropertyAndSkuIdMapping(Long productId) {
        Map<Object, Object> salePropertyAndSkuIdMap = new HashMap<>();
        List<Map> retMapList = salePropertyValueMapper.getSalePropertyAndSkuIdMapping(productId);
        for (Map retMap : retMapList) {
            salePropertyAndSkuIdMap.put(retMap.get("sale_property_value_id"), retMap.get("sku_id"));
        }
        return salePropertyAndSkuIdMap;

    }

    @Override
    public List<ProductSalePropertyKey> getSpuSalePropertyAndSelected(Long productId, Long skuId) {
        return salePropertyKeyMapper.getSpuSalePropertyAndSelected(productId,skuId);
    }

}
