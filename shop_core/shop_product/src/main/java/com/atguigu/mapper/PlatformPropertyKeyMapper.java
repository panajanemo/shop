package com.atguigu.mapper;

import com.atguigu.entity.PlatformPropertyKey;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 属性表 Mapper 接口
 * </p>
 *
 * @author panajanemo
 * @since 2023-02-10
 */
public interface PlatformPropertyKeyMapper extends BaseMapper<PlatformPropertyKey> {
                                                                //添加@Param()使xml文件test能识别
    List<PlatformPropertyKey> getPlatformPropertyKeyByCategoryId(@Param("category1Id")Long category1Id, @Param("category2Id")Long category2Id, @Param("category3Id")Long category3Id);

    List<PlatformPropertyKey> getPlatformPropertyByCategoryId(@Param("category1Id")Long category1Id, @Param("category2Id")Long category2Id, @Param("category3Id")Long category3Id);
}
