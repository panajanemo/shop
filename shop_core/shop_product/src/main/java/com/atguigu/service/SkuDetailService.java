package com.atguigu.service;

import com.atguigu.entity.ProductSalePropertyKey;
import com.atguigu.entity.SkuInfo;

import java.util.List;
import java.util.Map;

/**
 * @author panajanemo
 * @version 1.0.0
 * @title SkuDetailService
 * @description TODO
 * @create 2023/2/23 18:04
 */
public interface SkuDetailService {
    SkuInfo getSkuInfo(Long skuId);

    Map<Object,Object> getSalePropertyAndSkuIdMapping(Long productId);

    List<ProductSalePropertyKey> getSpuSalePropertyAndSelected(Long productId, Long skuId);
}
