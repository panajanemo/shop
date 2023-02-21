package com.atguigu.service.impl;

import com.atguigu.entity.ProductImage;
import com.atguigu.entity.ProductSalePropertyKey;
import com.atguigu.entity.ProductSalePropertyValue;
import com.atguigu.entity.ProductSpu;
import com.atguigu.mapper.ProductSpuMapper;
import com.atguigu.service.ProductImageService;
import com.atguigu.service.ProductSalePropertyKeyService;
import com.atguigu.service.ProductSalePropertyValueService;
import com.atguigu.service.ProductSpuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author panajanemo
 * @since 2023-02-20
 */
@Service
public class ProductSpuServiceImpl extends ServiceImpl<ProductSpuMapper, ProductSpu> implements ProductSpuService {
    @Autowired
    private ProductImageService imageService;
    @Autowired
    private ProductSalePropertyKeyService salePropertyKeyService;
    @Autowired
    private ProductSalePropertyValueService salePropertyValueService;

    @Transactional
    @Override
    public void saveProductSpu(ProductSpu productSpu) {
        //保存spu的基本信息
        baseMapper.insert(productSpu);

        //保存spu的图片信息
        List<ProductImage> productImageList = productSpu.getProductImageList();
        if (!CollectionUtils.isEmpty(productImageList)) {              //工具类判断是否为空
            for (ProductImage productImage : productImageList) {
                productImage.setProductId(productSpu.getId());
            }
            imageService.saveBatch(productImageList);   //批量保存
        }

        //保存spu的销售属性key
        List<ProductSalePropertyKey> salePropertyKeyList = productSpu.getSalePropertyKeyList();
        if (!CollectionUtils.isEmpty(salePropertyKeyList)) {              //工具类判断是否为空
            for (ProductSalePropertyKey productSalePropertyKey : salePropertyKeyList) {
                productSalePropertyKey.setProductId(productSpu.getId());

                //保存spu的销售属性value
                List<ProductSalePropertyValue> salePropertyValueList = productSalePropertyKey.getSalePropertyValueList();
                if (!CollectionUtils.isEmpty(salePropertyValueList)) {              //工具类判断是否为空
                    for (ProductSalePropertyValue productSalePropertyValue : salePropertyValueList) {
                        productSalePropertyValue.setProductId(productSpu.getId());
                        productSalePropertyValue.setSalePropertyKeyName(productSalePropertyKey.getSalePropertyKeyName());
                    }
                    salePropertyValueService.saveBatch(salePropertyValueList);      //批量保存
                }

            }
            salePropertyKeyService.saveBatch(salePropertyKeyList);      //批量保存
        }
    }
}