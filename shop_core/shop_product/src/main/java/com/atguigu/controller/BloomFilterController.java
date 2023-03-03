package com.atguigu.controller;


import com.atguigu.entity.SkuInfo;
import com.atguigu.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 品牌表 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2022-08-26
 */
@RestController
@RequestMapping("/init")
public class BloomFilterController {
    @Autowired
    private RBloomFilter skuBloomFilter;
    @Autowired
    private SkuInfoService skuInfoService;

    //TODO 定时任务 每天晚上或者其他时间 同步数据库与布隆过滤器里面的内容
    @GetMapping("/sku/bloom")
    public String setBloom() {
        //如果数据库有变化，还需要同步布隆过滤器，把所有的数据删除，再重新获取
        skuBloomFilter.delete();
        //初始化布隆过滤器的大小与容错率
        skuBloomFilter.tryInit(10000, 0.001);

        //加载数据库中所有的id
        QueryWrapper<SkuInfo> wrapper = new QueryWrapper<>();
        wrapper.select("id");
        List<SkuInfo> skuInfoList = skuInfoService.list(wrapper);
        for (SkuInfo skuInfo : skuInfoList) {
            Long skuId = skuInfo.getId();
            //把他放入到布隆过滤器里面
            skuBloomFilter.add(skuId);
        }
        return "success";
    }




}

