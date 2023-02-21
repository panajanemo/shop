package com.atguigu.controller;


import com.atguigu.entity.ProductImage;
import com.atguigu.entity.ProductSalePropertyKey;
import com.atguigu.result.RetVal;
import com.atguigu.service.ProductImageService;
import com.atguigu.service.ProductSalePropertyKeyService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 库存单元表 前端控制器
 * </p>
 *
 * @author panajanemo
 * @since 2023-02-21
 */
@RestController
@RequestMapping("/product")
public class SkuController {
    @Autowired
    private ProductSalePropertyKeyService salePropertyKeyService;
    @Autowired
    private ProductImageService productImageService;

    /**
     * @description 根据spuId查询所有的销售属性  http://127.0.0.1/product/querySalePropertyByProductId/12
     * @param spuId
     * @return RetVal
     * @author panajanemo
     * @time 2023/2/21 18:16
     */
    @GetMapping("querySalePropertyByProductId/{spuId}")
    public RetVal querySalePropertyByProductId(@PathVariable Long spuId) {
        List<ProductSalePropertyKey> productSalePropertyKeyList = salePropertyKeyService.querySalePropertyByProductId(spuId);
        return RetVal.ok(productSalePropertyKeyList);
    }

    /**
     * @description 根据spuId查询所有spu图片  http://127.0.0.1/product/queryProductImageByProductId/12
     * @param spuId
     * @return RetVal
     * @author panajanemo
     * @time 2023/2/21 18:16
     */
    @GetMapping("queryProductImageByProductId/{spuId}")
    public RetVal queryProductImageByProductId(@PathVariable Long spuId) {
        QueryWrapper<ProductImage> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", spuId);
        List<ProductImage> productImageList = productImageService.list(wrapper);
        return RetVal.ok(productImageList);
    }
}

