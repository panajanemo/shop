package com.atguigu.service.impl;

import com.atguigu.entity.BaseBrand;
import com.atguigu.exception.SleepUtils;
import com.atguigu.mapper.BaseBrandMapper;
import com.atguigu.service.BaseBrandService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 品牌表 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2022-08-26
 */
@Service
public class BaseBrandServiceImpl extends ServiceImpl<BaseBrandMapper, BaseBrand> implements BaseBrandService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @param
     * @description redis业务
     * @author panajanemo
     * @time 2023/2/27 15:59
     */

    private void doBusiness() {
        String value = (String) redisTemplate.opsForValue().get("num");
        if (StringUtils.isEmpty(value)) {
            redisTemplate.opsForValue().set("num", "1");
        } else {
            int num = Integer.parseInt(value);
            //对它进行加1操作
            redisTemplate.opsForValue().set("num", String.valueOf(++num));
        }
    }

    /**
     * @param
     * @description 什么都不加，压测时不正确
     * @author panajanemo
     * @time 2023/2/27 15:58
     */
    public void setNum01() {
        String value = (String) redisTemplate.opsForValue().get("num");
        if (StringUtils.isEmpty(value)) {
            redisTemplate.opsForValue().set("num", "1");
        } else {
            int num = Integer.parseInt(value);
            //对它进行加1操作
            redisTemplate.opsForValue().set("num", String.valueOf(++num));
        }
    }

    //@Override

    /**
     * @param
     * @description 仅加锁，多个服务启动时不正确
     * @author panajanemo
     * @time 2023/2/27 15:58
     */
    public synchronized void setNum02() {
        String value = (String) redisTemplate.opsForValue().get("num");
        if (StringUtils.isEmpty(value)) {
            redisTemplate.opsForValue().set("num", "1");
        } else {
            int num = Integer.parseInt(value);
            //对它进行加1操作
            redisTemplate.opsForValue().set("num", String.valueOf(++num));
        }
    }

    /**
     * @param
     * @description 分布式锁案例一，利用redis加锁，但如果doBusiness出现异常，可能导致锁一直占用，无法释放
     * @author panajanemo
     * @time 2023/2/27 16:02
     */
    public synchronized void setNum03() {
        //利用redis的setnx命令
        boolean accquireLock = redisTemplate.opsForValue().setIfAbsent("lock", "ok");
        if (accquireLock) {
            //拿到锁 就可以执行业务 如果doBusiness出现异常 可能导致锁一直占用 无法释放
            doBusiness();
            //业务做完了之后需要删除锁
            redisTemplate.delete("lock");
        } else {
            //如果没有拿到锁 递归
            setNum();
        }
    }

    /**
     * @param
     * @description 分布式锁案例二，添加过期时间，但由于锁的过期时间和业务时间不一致，可能会删除其他线程的锁
     * @author panajanemo
     * @time 2023/2/27 16:02
     */
    public synchronized void setNum04() {
        //利用redis的setnx命令                                                                     //TimeUnit.SECONDS:过期时间
        boolean accquireLock = redisTemplate.opsForValue().setIfAbsent("lock", "ok", 3, TimeUnit.SECONDS);
        if (accquireLock) {
            //拿到锁 就可以执行业务
            doBusiness();
            //业务做完了之后需要删除锁
            redisTemplate.delete("lock");
        } else {
            //如果没有拿到锁 递归
            setNum();
        }
    }

    /**
     * @param
     * @description 分布式锁案例三，设置锁的值为一个UUID，删除锁之前先给一个判断
     * 但可能线程1执行锁的释放时刚好线程2获得锁，导致线程2的锁被删除，即判断和删除这两个语句缺乏原子性
     * @author panajanemo
     * @time 2023/2/27 16:02
     */
    public synchronized void setNum05() {
        //放一个锁的标记
        String token = UUID.randomUUID().toString();
        //利用redis的setnx命令
        boolean accquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
        if (accquireLock) {
            //拿到锁 就可以执行业务
            doBusiness();
            //业务做完了之后需要删除锁
            String redisToken = (String) redisTemplate.opsForValue().get("lock");
            if (token.equals(redisToken)) {
                redisTemplate.delete("lock");
            }
        } else {
            //如果没有拿到锁 递归
            setNum();
        }
    }

    /**
     * @param
     * @description 分布式锁案例四，让判断删除两句话具备原子性
     * @author panajanemo
     * @time 2023/2/27 16:02
     */
    public synchronized void setNum06() {
        //放一个锁的标记
        String token = UUID.randomUUID().toString();
        //利用redis的setnx命令
        boolean accquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
        if (accquireLock) {
            //拿到锁 就可以执行业务
            doBusiness();
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            //把脚本封装到redisScript中
            redisScript.setScriptText(luaScript);
            //设置脚本运行完成之后返回的数据类型
            redisScript.setResultType(Long.class);
            //执行脚本
            redisTemplate.execute(redisScript, Arrays.asList("lock"), token);
            //业务做完了之后需要删除锁
            /**
             * 上面的脚本 相当于下面这一坨代码 把这个代码原子性了
             * String redisToken=(String)redisTemplate.opsForValue().get("lock");
             *             if(token.equals(redisToken)){
             *                 redisTemplate.delete("lock");
             *             }
             */

        } else {
            //如果没有拿到锁 递归
            setNum();
        }
    }

    /**
     * @param
     * @description 分布式锁优化一，递归改成自选
     * 但会出现死锁问题，即retryAccquireLock拿到锁后递归，执行到accquireLock时无法拿到锁而出现死锁
     * @author panajanemo
     * @time 2023/2/27 17:35
     */
    public void setNum07() {
        //还有很多操作要做 去执行查询其他逻辑 查缓存等等业务 1000行
        String token = UUID.randomUUID().toString();
        boolean accquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.MINUTES);
        if (accquireLock) {
            doBusiness();
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(luaScript);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Arrays.asList("lock"), token);
        } else {
            //自旋，目的是为了拿锁
            while (true) {
                SleepUtils.millis(50);
                //重复去拿锁
                boolean retryAccquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.MINUTES);
                if (retryAccquireLock) {
                    //拿到锁以后就不需要自旋了
                    break;
                }
            }
            setNum();
        }
    }

    /**
     * @param
     * @description 分布式锁优化二，重入锁设计，解决上面问题
     * 但存在token为空导致lock无法删除情况
     * @author panajanemo
     * @time 2023/2/27 17:47
     */
    Map<Thread, Boolean> threadMap = new HashMap<>();

    public void setNum08() {
        //还有很多操作要做 去执行查询其他逻辑 查缓存等等业务 1000行
        Boolean flag = threadMap.get(Thread.currentThread());
        boolean accquireLock = false;
        String token = null;
        //第一次来 还没有参与自旋
        if (flag == null || flag == false) {
            token = UUID.randomUUID().toString();
            accquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.MINUTES);
        } else {
            //代表已经拿到锁了
            accquireLock = true;
        }
        if (accquireLock) {
            doBusiness();
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(luaScript);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Arrays.asList("lock"), token);
        } else {
            //自旋 目的是为了拿锁
            while (true) {
                SleepUtils.millis(50);
                //重复去拿锁
                boolean retryAccquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.MINUTES);
                if (retryAccquireLock) {
                    //拿到锁以后就不需要自旋了,把锁的情况标记一下
                    threadMap.put(Thread.currentThread(), true);
                    break;
                }
            }
            setNum();
        }
    }



    /**
     * @description 分布式锁优化三，Map<Thread, Boolean>修改为Map<Thread, String>，存储token，解决token为空问题
     * 但存储的token，会导致map出现内存泄露问题
     * @param
     * @author panajanemo
     * @time 2023/2/27 21:56
     */

    Map<Thread, String> threadMap1 = new HashMap<>();

    public void setNum09() {
        //还有很多操作要做 去执行查询其他逻辑 查缓存等等业务 1000行
        String token = threadMap1.get(Thread.currentThread());
        boolean accquireLock = false;
        //第一次来 还没有参与自旋
        if (token == null) {
            token = UUID.randomUUID().toString();
            accquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.MINUTES);
        } else {
            //代表已经拿到锁了
            accquireLock = true;
        }
        if (accquireLock) {
            doBusiness();
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(luaScript);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Arrays.asList("lock"), token);
        } else {
            //自旋 目的是为了拿锁
            while (true) {
                SleepUtils.millis(50);
                //重复去拿锁
                boolean retryAccquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.MINUTES);
                if (retryAccquireLock) {
                    //拿到锁以后就不需要自旋了,把锁的情况标记一下
                    threadMap1.put(Thread.currentThread(), token);
                    break;
                }
            }
            setNum();
        }
    }

    /**
     * @description 分布式锁优化四，threadMap->threadLocal，同时解决内存泄漏
     * 但没有自动续期的功能
     * @param
     * @author panajanemo
     * @time 2023/2/27 22:24
     */
    //
    ThreadLocal<String> threadLocal = new ThreadLocal<>();
    public void setNum10() {
        //还有操很多作要做 去执行查询其他逻辑 查缓存等等业务 1000行
        String token = threadLocal.get();
        boolean accquireLock = false;
        //第一次来 还没有参与自旋
        if (token == null) {
            token = UUID.randomUUID().toString();                                       //没有自动续期
            accquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.MINUTES);
        } else {
            //代表已经拿到锁了
            accquireLock = true;
        }
        if (accquireLock) {
            doBusiness();
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(luaScript);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Arrays.asList("lock"), token);
            //擦屁股 防止内存泄漏
            threadLocal.remove();
        } else {
            //自旋 目的是为了拿锁
            while (true) {
                SleepUtils.millis(50);
                //重复去拿锁
                boolean retryAccquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.MINUTES);
                if (retryAccquireLock) {
                    //拿到锁以后就不需要自旋了,把锁的情况标记一下
                    threadLocal.set(token);
                    break;
                }
            }
            setNum();
        }
    }

    /**
     * @description redisson测试
     * @author panajanemo
     * @time 2023/2/28 21:49
     */

    @Autowired
    private RedissonClient redissonClient;
    @Override
    public void setNum() {
        RLock lock = redissonClient.getLock("lock");
        //加锁
        lock.lock();
        try {
            doBusiness();
        }finally {
            lock.unlock();
        }

    }
}
