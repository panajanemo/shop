package com.atguigu.controller;

import com.atguigu.entity.ProductImage;
import com.atguigu.entity.ProductSalePropertyKey;
import com.atguigu.entity.SkuInfo;
import com.atguigu.result.RetVal;
import com.atguigu.service.ProductImageService;
import com.atguigu.service.ProductSalePropertyKeyService;
import com.atguigu.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * @param spuId
     * @return RetVal
     * @description 根据spuId查询所有的销售属性  http://127.0.0.1/product/querySalePropertyByProductId/12
     * @author panajanemo
     * @time 2023/2/21 18:16
     */
    @GetMapping("querySalePropertyByProductId/{spuId}")
    public RetVal querySalePropertyByProductId(@PathVariable Long spuId) {
        List<ProductSalePropertyKey> productSalePropertyKeyList = salePropertyKeyService.querySalePropertyByProductId(spuId);
        return RetVal.ok(productSalePropertyKeyList);
    }

    /**
     * @param spuId
     * @return RetVal
     * @description 根据spuId查询所有spu图片  http://127.0.0.1/product/queryProductImageByProductId/12
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

    /**
     * @param skuInfo
     * @return RetVal
     * @description 保存商品sku http://127.0.0.1/product/saveSkuInfo
     * @author panajanemo
     * @time 2023/2/22 10:39
     */
    @PostMapping("saveSkuInfo")
    public RetVal saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        skuInfoService.saveSkuInfo(skuInfo);
        return RetVal.ok();
    }

    /**
     * @param currentPageNum
     * @param pageSize
     * @return RetVal
     * @description sku列表显示 http://127.0.0.1/product/querySkuInfoByPage/1/10
     * @author panajanemo
     * @time 2023/2/22 10:42
     */
    @GetMapping("querySkuInfoByPage/{currentPageNum}/{pageSize}")
    public RetVal queryProductSpuByPage(@PathVariable Long currentPageNum,
                                        @PathVariable Long pageSize) {
        IPage<SkuInfo> page = new Page<>(currentPageNum, pageSize);
        skuInfoService.page(page, null);
        return RetVal.ok(page);
    }

    /**
     * @param skuId
     * @return RetVal
     * @description 商品上架 http://192.168.11.218/product/onSale/24
     * @author panajanemo
     * @time 2023/2/22 16:18
     */
    @GetMapping("onSale/{skuId}")
    public RetVal onSale(@PathVariable Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        //1：是 0：否
        skuInfo.setIsSale(1);
        skuInfo.setId(skuId);
        skuInfoService.updateById(skuInfo);
        //TODO 后面还会涉及到搜索环节再继续编写代码
        return RetVal.ok();
    }

    /**
     * @param skuId
     * @return RetVal
     * @description 商品上架 http://192.168.11.218/product/offSale/24
     * @author panajanemo
     * @time 2023/2/22 16:19
     */
    @GetMapping("offSale/{skuId}")
    public RetVal offSale(@PathVariable Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        //1：是 0：否
        skuInfo.setIsSale(0);
        skuInfo.setId(skuId);
        skuInfoService.updateById(skuInfo);
        //TODO 后面还会涉及到搜索环节再继续编写代码
        return RetVal.ok();
    }


}
