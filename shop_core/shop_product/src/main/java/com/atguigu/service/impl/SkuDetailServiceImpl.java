package com.atguigu.service.impl;

import com.atguigu.cache.ShopCache;
import com.atguigu.constant.RedisConst;
import com.atguigu.entity.ProductSalePropertyKey;
import com.atguigu.entity.SkuImage;
import com.atguigu.entity.SkuInfo;
import com.atguigu.exception.SleepUtils;
import com.atguigu.mapper.ProductSalePropertyKeyMapper;
import com.atguigu.mapper.SkuSalePropertyValueMapper;
import com.atguigu.service.SkuDetailService;
import com.atguigu.service.SkuImageService;
import com.atguigu.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author panajanemo
 * @version 1.0.0
 * @title SkuDetailServiceImpl
 * @description TODO
 * @create 2023/2/23 18:05
 */
@Service
public class
SkuDetailServiceImpl implements SkuDetailService {
    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImageService skuImageService;

    @Autowired
    private SkuSalePropertyValueMapper salePropertyValueMapper;

    @Autowired
    private ProductSalePropertyKeyMapper salePropertyKeyMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RBloomFilter skuBloomFilter;

    /**
     * @param skuId
     * @return SkuInfo
     * @description 根据商品skuId查询商品的基本信息
     * @author panajanemo
     * @time 2023/2/24 15:29
     */
    @ShopCache
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        SkuInfo skuInfo = getSkuInfoFromDB(skuId);
        //SkuInfo skuInfo = getSkuInfoFromRedis(skuId);
        //SkuInfo skuInfo = getSkuInfoFromRedisWithThreadLocal(skuId);
        //SkuInfo skuInfo = getSkuInfoFromRedisson(skuId);
        return skuInfo;
    }

    /**
     * @description redisson实现锁
     * @author panajanemo
     * @time 2023/3/1 16:16
     */
    private SkuInfo getSkuInfoFromRedisson(Long skuId) {
        //sku:24:info
        String cacheKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        //从缓存中查询数据
        SkuInfo skuInfoFromRedis = (SkuInfo) redisTemplate.opsForValue().get(cacheKey);
        //如果缓存为空
        if (skuInfoFromRedis == null) {
            String lockKey = "lock-" + skuId;
            RLock lock = redissonClient.getLock(lockKey);
            lock.lock();
            try {
                boolean flag = skuBloomFilter.contains(skuId);
                SkuInfo skuInfoDb=null;
                if(flag){
                    skuInfoDb = getSkuInfoFromDB(skuId);
                    //把数据放入缓存
                    redisTemplate.opsForValue().set(cacheKey,skuInfoDb, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                    return skuInfoDb;
                }
            } finally {
                lock.unlock();
            }
        }
        return skuInfoFromRedis;
    }

    /**
     * @description 利用lua+redis+threadLocl实现分布式锁
     * @author panajanemo
     * @time 2023/2/28 18:54
     */
    ThreadLocal<String> threadLocal = new ThreadLocal<>();

    private SkuInfo getSkuInfoFromRedisWithThreadLocal(Long skuId) {
        //sku:24:info
        String cacheKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        //从缓存中查询数据
        SkuInfo skuInfoFromRedis = (SkuInfo) redisTemplate.opsForValue().get(cacheKey);
        //如果缓存为空，则存储数据到缓存
        if (skuInfoFromRedis == null) {
            //还有操很多作要做 去执行查询其他逻辑 查缓存等等业务 1000行
            String token = threadLocal.get();
            //定义一个锁的名称
            String lockKey = "lock-" + skuId;
            boolean accquireLock = false;
            //第一次来 还没有参与自旋
            if (token == null) {
                token = UUID.randomUUID().toString();
                accquireLock = redisTemplate.opsForValue().setIfAbsent(lockKey, token, 1, TimeUnit.SECONDS);
            } else {
                //代表已经拿到锁了
                accquireLock = true;
            }
            if (accquireLock) {
                SkuInfo skuInfoDB = doBusiness(skuId, cacheKey);
                String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                redisScript.setScriptText(luaScript);
                redisScript.setResultType(Long.class);
                redisTemplate.execute(redisScript, Arrays.asList(lockKey), token);
                //擦屁股 防止内存泄漏
                threadLocal.remove();
                return skuInfoDB;
            } else {
                //自旋 目的是为了拿锁
                for (; ; ) {
                    SleepUtils.millis(50);
                    //重复去拿锁
                    boolean retryAccquireLock = redisTemplate.opsForValue().setIfAbsent(lockKey, token, 1, TimeUnit.SECONDS);
                    if (retryAccquireLock) {
                        //拿到锁以后就不需要自旋了,把锁的情况标记一下
                        threadLocal.set(token);
                        break;
                    }
                }
                return getSkuInfoFromRedisWithThreadLocal(skuId);
            }
        }
        //缓存不为空，直接返回数据
        return skuInfoFromRedis;
    }

    private SkuInfo getSkuInfoFromRedis(Long skuId) {
        //sku:24:info
        String cacheKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        //从缓存中查询数据
        SkuInfo skuInfoFromRedis = (SkuInfo) redisTemplate.opsForValue().get(cacheKey);
        //如果缓存为空，则存储数据
        if (skuInfoFromRedis == null) {
            return doBusiness(skuId, cacheKey);
        }
        //如果缓存不为空，则直接返回数据
        return skuInfoFromRedis;
    }

    private SkuInfo doBusiness(Long skuId, String cacheKey) {
        SkuInfo skuInfoDb = getSkuInfoFromDB(skuId);
        //把数据放入缓存
        redisTemplate.opsForValue().set(cacheKey, skuInfoDb, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
        return skuInfoDb;
    }

    private SkuInfo getSkuInfoFromDB(Long skuId) {
        //1.查询商品基本信息
        SkuInfo skuInfo = skuInfoService.getById(skuId);
        //2.查询商品图片信息
        if (skuInfo != null) {
            QueryWrapper<SkuImage> wrapper = new QueryWrapper<>();
            wrapper.eq("sku_id", skuId);
            List<SkuImage> skuImageList = skuImageService.list(wrapper);
            skuInfo.setSkuImageList(skuImageList);
        }
        return skuInfo;
    }

    /**
     * @param productId
     * @return Map<Object>
     * @description 销售属性组合与skuId的对应关系
     * @author panajanemo
     * @time 2023/2/24 15:27
     */
    @Override
    public Map<Object, Object> getSalePropertyAndSkuIdMapping(Long productId) {
        Map<Object, Object> salePropertyAndSkuIdMap = new HashMap<>();
        List<Map> retMapList = salePropertyValueMapper.getSalePropertyAndSkuIdMapping(productId);
        for (Map retMap : retMapList) {
            salePropertyAndSkuIdMap.put(retMap.get("sale_property_value_id"), retMap.get("sku_id"));
        }
        return salePropertyAndSkuIdMap;

    }

    /**
     * @param productId
     * @param skuId
     * @return List<ProductSalePropertyKey>
     * @description sku对应的销售属性(一份)所有的销售属性(全份)
     * @author panajanemo
     * @time 2023/2/25 21:23
     */

    @Override
    public List<ProductSalePropertyKey> getSpuSalePropertyAndSelected(Long productId, Long skuId) {
        return salePropertyKeyMapper.getSpuSalePropertyAndSelected(productId, skuId);
    }

}
