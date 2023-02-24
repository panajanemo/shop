package com.atguigu;

import com.atguigu.entity.BaseCategoryView;
import com.atguigu.entity.ProductSalePropertyKey;
import com.atguigu.entity.SkuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(value = "shop-product")
public interface ProductFeignClient {
    //1.根据商品skuId查询商品的基本信息
    @GetMapping("/sku/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId);

    //2.根据三级分类id查询商品的分类信息 //select * from base_category_view a where a.category3_id=11
    @GetMapping("/sku/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable Long category3Id);

    //3.根据skuId查询商品的实时价格
    @GetMapping("/sku/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable Long skuId);

    //4.销售属性组合与skuId的对应关系
    @GetMapping("/sku/getSalePropertyAndSkuIdMapping/{productId}")
    public Map<Object, Object> getSalePropertyAndSkuIdMapping(@PathVariable Long productId);

    //5.该sku对应的销售属性(一份)所有的销售属性(全份)
    @GetMapping("/sku/getSpuSalePropertyAndSelected/{productId}/{skuId}")
    public List<ProductSalePropertyKey> getSpuSalePropertyAndSelected(@PathVariable Long productId, @PathVariable Long skuId);
}