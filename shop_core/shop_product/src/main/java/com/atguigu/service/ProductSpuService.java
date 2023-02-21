package com.atguigu.service;

import com.atguigu.entity.ProductSpu;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author panajanemo
 * @since 2023-02-20
 */
public interface ProductSpuService extends IService<ProductSpu> {

    void saveProductSpu(ProductSpu productSpu);
}
