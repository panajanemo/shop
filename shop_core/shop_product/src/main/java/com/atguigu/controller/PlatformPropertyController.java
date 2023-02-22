package com.atguigu.controller;


import com.atguigu.entity.PlatformPropertyKey;
import com.atguigu.entity.PlatformPropertyValue;
import com.atguigu.result.RetVal;
import com.atguigu.service.PlatformPropertyKeyService;
import com.atguigu.service.PlatformPropertyValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 属性表 前端控制器
 * </p>
 *
 * @author panajanemo
 * @since 2023-02-10
 */
@RestController
@RequestMapping("/product")
public class PlatformPropertyController {

    @Autowired
    private PlatformPropertyKeyService propertyKeyService;

    @Autowired
    private PlatformPropertyValueService propertyValueService;

    /*
     * @description 根据商品分类id查询平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return RetVal
     * @author panajanemo
     * @time 2023/2/11 18:03
     */
    @GetMapping("getPlatformPropertyByCategoryId/{category1Id}/{category2Id}/{category3Id}")
    public RetVal getPlatformPropertyByCategoryId(@PathVariable Long category1Id,
                                                  @PathVariable Long category2Id,
                                                  @PathVariable Long category3Id) {
        List<PlatformPropertyKey> platformPropertyKeyList = propertyKeyService.getPlatformPropertyByCategoryId(category1Id, category2Id, category3Id);
        return RetVal.ok(platformPropertyKeyList);
    }

    /**
     * @param propertyKeyId
     * @return RetVal
     * @description 根据平台属性key的id查询平台属性值
     * @author panajanemo
     * @time 2023/2/12 0:15
     */
    @GetMapping("getPropertyValueByPropertyKeyId/{propertyKeyId}")
    public RetVal getPropertyValueByPropertyKeyId(@PathVariable Long propertyKeyId) {
        QueryWrapper<PlatformPropertyValue> wrapper = new QueryWrapper<>();
        wrapper.eq("property_key_id", propertyKeyId);
        List<PlatformPropertyValue> platformPropertyValueList = propertyValueService.list(wrapper);
        return RetVal.ok(platformPropertyValueList);
    }

    /**
     * @param platformPropertyKey
     * @return RetVal
     * @description 保存/修改平台属性 由于请求是post类型 传的json http://127.0.0.1:8000/product/savePlatformProperty
     * @author panajanemo
     * @time 2023/2/22 2:15
     */

    @PostMapping("savePlatformProperty")
    public RetVal savePlatformProperty(@RequestBody PlatformPropertyKey platformPropertyKey) {
        propertyKeyService.savePlatformProperty(platformPropertyKey);
        return RetVal.ok();
    }

}

