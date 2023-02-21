package com.atguigu.service;

import com.atguigu.entity.ProductSalePropertyKey;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * spu销售属性 服务类
 * </p>
 *
 * @author panajanemo
 * @since 2023-02-20
 */
public interface ProductSalePropertyKeyService extends IService<ProductSalePropertyKey> {
    List<ProductSalePropertyKey> querySalePropertyByProductId(Long spuId);
}
