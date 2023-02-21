package com.atguigu.service.impl;

import com.atguigu.entity.PlatformPropertyKey;
import com.atguigu.entity.PlatformPropertyValue;
import com.atguigu.mapper.PlatformPropertyKeyMapper;
import com.atguigu.service.PlatformPropertyKeyService;
import com.atguigu.service.PlatformPropertyValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 属性表 服务实现类
 * </p>
 *
 * @author panajanemo
 * @since 2023-02-10
 */
@Service
public class PlatformPropertyKeyServiceImpl extends ServiceImpl<PlatformPropertyKeyMapper, PlatformPropertyKey> implements PlatformPropertyKeyService {

    @Autowired
    private PlatformPropertyValueService propertyValueService;

    /**
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return List<PlatformPropertyKey>
     * @description 第一种写法：先查key，再通过key查value，但效率不高
     * @author panajanemo
     * @time 2023/2/11 18:33
     */
    //@Override
    //public List<PlatformPropertyKey> getPlatformPropertyByCategoryId(Long category1Id, Long category2Id, Long category3Id) {
    //    //1.根据商品分类id查询平台属性名称 代码review
    //    List<PlatformPropertyKey> platformPropertyKeyList = baseMapper.getPlatformPropertyKeyByCategoryId(category1Id, category2Id, category3Id);
    //    //2.根据商品分类id查询平台属性值
    //    for (PlatformPropertyKey platformPropertyKey : platformPropertyKeyList) {             //遍历keylist
    //        QueryWrapper<PlatformPropertyValue> wrapper = new QueryWrapper<>();
    //        wrapper.eq("property_key_id", platformPropertyKey.getId());
    //        List<PlatformPropertyValue> propertyValueList = propertyValueService.list(wrapper);     //生成valuelist
    //        platformPropertyKey.setPropertyValueList(propertyValueList);
    //    }
    //    return platformPropertyKeyList;
    //}

    /**
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return List<PlatformPropertyKey>
     * @description 第二种写法 只用一条sal实现 是一种一对多的关系
     * @author panajanemo
     * @time 2023/2/11 23:05
     */

    @Override
    public List<PlatformPropertyKey> getPlatformPropertyByCategoryId(Long category1Id, Long category2Id, Long category3Id) {
        return baseMapper.getPlatformPropertyByCategoryId(category1Id, category2Id, category3Id);
    }

    @Transactional
    @Override
    public void savePlatformProperty(PlatformPropertyKey platformPropertyKey) {
        //修改平台属性
        if (platformPropertyKey.getId() != null) {
            baseMapper.updateById(platformPropertyKey);
            //删除该平台属性已有属性值
            QueryWrapper<PlatformPropertyValue> wrapper = new QueryWrapper<>();
            wrapper.eq("property_key_id", platformPropertyKey.getId());
            propertyValueService.remove(wrapper);
        } else {
            //a.保存平台属性key的名称
            baseMapper.insert(platformPropertyKey);
        }
        //b.保存平台属性value集合
        List<PlatformPropertyValue> propertyValueList = platformPropertyKey.getPropertyValueList();
        if (!CollectionUtils.isEmpty(propertyValueList)) {              //工具类判断是否为空
            for (PlatformPropertyValue platformPropertyValue : propertyValueList) {
                platformPropertyValue.setPropertyKeyId(platformPropertyKey.getId());
            }
            propertyValueService.saveBatch(propertyValueList);
        }
    }
}
