package com.atguigu.controller;


import com.atguigu.exception.SleepUtils;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 品牌表 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2022-08-26
 */
@RestController
@RequestMapping("/product")
public class RedissonController {
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 1.最简单的分布式锁
     * a.默认有个30s的过期时间   internalLockLeaseTime=30s=lockWatchdogTimeout
     * b.每隔10s就会自动续期     internalLockLeaseTime / 3
     * 看门狗机制，将我们的锁自动续期
     *
     * @return
     */
    @GetMapping("lock")
    public String lock() {
        RLock lock = redissonClient.getLock("lock");
        //加锁 一直去等待拿锁 自动续期  可重入
        lock.lock();
        String uuid = UUID.randomUUID().toString();
        try {
            lock.lock();
            SleepUtils.sleep(2);
            System.out.println(Thread.currentThread().getName() + "执行业务" + uuid);
        } finally {
            lock.unlock();
        }
        return Thread.currentThread().getName() + "执行业务" + uuid;
    }

    @GetMapping("lock2")
    public String lock2() {
        RLock lock = redissonClient.getLock("lock");
        //加锁 一直去等待拿锁 自动续期  可重入
        String uuid = UUID.randomUUID().toString();
        try {
            lock.tryLock(10, 20, TimeUnit.SECONDS);
            SleepUtils.sleep(2);
            System.out.println(Thread.currentThread().getName() + "执行业务" + uuid);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return Thread.currentThread().getName() + "执行业务" + uuid;
    }

    //测试写锁
    String uuid = "";

    @GetMapping("write")
    public String write() {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("rwLock");
        RLock writeLock = rwLock.writeLock();
        try {
            writeLock.lock();
            SleepUtils.sleep(12);
            uuid = UUID.randomUUID().toString();
            System.out.println(Thread.currentThread().getName() + "执行业务" + uuid);
        } finally {
            writeLock.unlock();
        }
        return Thread.currentThread().getName() + "执行业务" + uuid;
    }

    @GetMapping("read")
    public String read() {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("rwLock");
        RLock readLock = rwLock.readLock();
        try {
            readLock.lock();
            return uuid;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 测试Semaphore 停车场有5个车位
     */
    @GetMapping("park")
    public String park() throws Exception {
        RSemaphore parkStation = redissonClient.getSemaphore("park_station");
        //信号量减1
        parkStation.acquire(1);
        System.out.println(Thread.currentThread().getName() + "找到车位");
        return Thread.currentThread().getName() + "找到车位";
    }

    @GetMapping("left")
    public String left() throws Exception {
        RSemaphore parkStation = redissonClient.getSemaphore("park_station");
        //信号量加1
        parkStation.release(1);
        System.out.println(Thread.currentThread().getName() + "left");
        return Thread.currentThread().getName() + "left";
    }


    @GetMapping("leftClassRoom")
    public String leftClassRoom() throws Exception {
        RCountDownLatch leftClassroom = redissonClient.getCountDownLatch("left_classroom");
        //有人走了 数量减一
        leftClassroom.countDown();
        return Thread.currentThread().getName() + "学员离开";
    }

    @GetMapping("lockDoor")
    public String lockDoor() throws Exception {
        RCountDownLatch leftClassroom = redissonClient.getCountDownLatch("left_classroom");
        //假如6个人
        leftClassroom.trySetCount(6);
        leftClassroom.await();
        return Thread.currentThread().getName() + "班长离开";
    }

    //公平锁
    @GetMapping("fairLock/{id}")
    public String fairLock(@PathVariable Long id) throws Exception {
        RLock fairLock = redissonClient.getFairLock("fair-lock");
        fairLock.lock();
        SleepUtils.sleep(8);
        System.out.println("公平锁-" + id);
        fairLock.unlock();
        return "success";
    }

    //非公平锁
    @GetMapping("unFairLock/{id}")
    public String unFairLock(@PathVariable Long id) throws Exception {
        RLock unFairLock = redissonClient.getLock("unfair-lock");
        unFairLock.lock();
        SleepUtils.sleep(8);
        System.out.println("非公平锁-" + id);
        unFairLock.unlock();
        return "success";
    }
}

