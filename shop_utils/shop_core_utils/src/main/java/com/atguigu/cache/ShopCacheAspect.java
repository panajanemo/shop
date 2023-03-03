package com.atguigu.cache;

import com.atguigu.constant.RedisConst;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class ShopCacheAspect {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RBloomFilter skuBloomFilter;

    @Around("@annotation(com.atguigu.cache.ShopCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint target) throws Throwable {
        //a.需要从目标方法上面去拿到参数skuId
        Object[] methodParams = target.getArgs();
        //b.拿到目标方法
        MethodSignature methodSignature = (MethodSignature) target.getSignature();
        Method targetMethod = methodSignature.getMethod();
        ShopCache shopCache = targetMethod.getAnnotation(ShopCache.class);
        //c.拿到注解里面的value的值 布隆开关
        String prefix = shopCache.prefix();
        boolean enableBloom = shopCache.enableBloom();
        //sku:24:info
        Object firtMethodParam = methodParams[0];
        String cacheKey = prefix + ":" + firtMethodParam;
        //从缓存中查询数据
        Object cacheObject = (Object) redisTemplate.opsForValue().get(cacheKey);
        //如果缓存不为空
        if (cacheObject == null) {
            String lockKey = "lock-" + firtMethodParam;
            //intern()确保两个线程拿到的锁是一模一样的
            synchronized (lockKey.intern()) {
                Object objectDb = null;
                if (enableBloom) {
                    boolean flag = skuBloomFilter.contains(firtMethodParam);
                    if (flag) {
                        //执行目标方法
                        objectDb = target.proceed();
                    }
                } else {
                    //执行目标方法
                    objectDb = target.proceed();
                }
                //把数据放入缓存
                redisTemplate.opsForValue().set(cacheKey, objectDb, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                return objectDb;
            }
        }
        return cacheObject;
    }

    //@Around("@annotation(com.atguigu.cache.ShopCache)")
    public Object cacheAroundAdvice2(ProceedingJoinPoint target) throws Throwable {
        //a.需要从目标方法上面去拿到参数skuId
        Object[] methodParams = target.getArgs();
        //b.拿到目标方法
        MethodSignature methodSignature = (MethodSignature) target.getSignature();
        Method targetMethod = methodSignature.getMethod();
        ShopCache shopCache = targetMethod.getAnnotation(ShopCache.class);
        //c.拿到注解里面的value的值 布隆开关
        String prefix = shopCache.prefix();
        boolean enableBloom = shopCache.enableBloom();
        //sku:24:info
        Object firtMethodParam = methodParams[0];
        String cacheKey = prefix + ":" + firtMethodParam;
        //从缓存中查询数据
        Object cacheObject = (Object) redisTemplate.opsForValue().get(cacheKey);
        //如果缓存不为空
        if (cacheObject == null) {
            String lockKey = "lock-" + firtMethodParam;
            RLock lock = redissonClient.getLock(lockKey);
            lock.lock();
            try {
                Object objectDb = null;
                if (enableBloom) {
                    boolean flag = skuBloomFilter.contains(firtMethodParam);
                    if (flag) {
                        //执行目标方法
                        objectDb = target.proceed();
                    }
                } else {
                    //执行目标方法
                    objectDb = target.proceed();
                }
                //把数据放入缓存
                redisTemplate.opsForValue().set(cacheKey, objectDb, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                return objectDb;
            } finally {
                lock.unlock();
            }
        }
        return cacheObject;
    }

    public static void main(String[] args) {
        String a = new String("49");
        String b = new String("49");
        System.out.println(a.intern() == b.intern());
        String c = "49";
        String d = "49";
        System.out.println(c == d);
    }

}
