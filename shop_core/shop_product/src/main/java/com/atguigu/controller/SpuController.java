package com.atguigu.controller;


import com.atguigu.entity.BaseSaleProperty;
import com.atguigu.entity.ProductSpu;
import com.atguigu.result.RetVal;
import com.atguigu.service.BaseSalePropertyService;
import com.atguigu.service.ProductSpuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author panajanemo
 * @since 2023-02-20
 */
@RestController
@RequestMapping("/product")
public class SpuController {

    @Autowired
    private ProductSpuService spuService;

    @Autowired
    private BaseSalePropertyService salePropertyService;

    /**
     * @param currentPageNum
     * @param pageSize
     * @param category3Id
     * @return RetVal
     * @description 根据商品分类id查询spu列表信息 http://127.0.0.1/product/queryProductSpuByPage/1/10/62
     * @author panajanemo
     * @time 2023/2/20 21:42
     */
    @GetMapping("queryProductSpuByPage/{currentPageNum}/{pageSize}/{category3Id}")
    public RetVal queryProductSpuByPage(@PathVariable Long currentPageNum, @PathVariable Long pageSize, @PathVariable Long category3Id) {
        IPage<ProductSpu> page = new Page<>(currentPageNum, pageSize);
        QueryWrapper<ProductSpu> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id", category3Id);
        spuService.page(page, null);
        return RetVal.ok(page);
    }

    /**
     * @param
     * @return RetVal
     * @description 查询所有的销售属性  http://127.0.0.1/product/queryAllSaleProperty
     * @author panajanemo
     * @time 2023/2/20 21:53
     */
    @GetMapping("{queryAllSaleProperty}")
    public RetVal queryAllSaleProperty() {
        List<BaseSaleProperty> salePropertyList = salePropertyService.list(null);
        return RetVal.ok(salePropertyList);
    }

    /**
     * @param productSpu
     * @return RetVal
     * @description 添加spu http://127.0.0.1/product/saveProductSpu
     * @author panajanemo
     * @time 2023/2/21 18:16
     */
    @PostMapping("saveProductSpu")
    public RetVal saveProductSpu(@RequestBody ProductSpu productSpu) {
        spuService.saveProductSpu(productSpu);
        return RetVal.ok();
    }
}

