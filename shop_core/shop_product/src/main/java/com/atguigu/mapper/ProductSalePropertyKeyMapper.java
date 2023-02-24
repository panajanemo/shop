package com.atguigu.mapper;

import com.atguigu.entity.ProductSalePropertyKey;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * spu销售属性 Mapper 接口
 * </p>
 *
 * @author panajanemo
 * @since 2023-02-20
 */
public interface ProductSalePropertyKeyMapper extends BaseMapper<ProductSalePropertyKey> {

    List<ProductSalePropertyKey> querySalePropertyByProductId(Long spuId);

    List<ProductSalePropertyKey> getSpuSalePropertyAndSelected(@Param("productId") Long productId, @Param("skuId") Long skuId);
}
