package com.atguigu;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BloomTest {
    @Autowired
    private RBloomFilter skuBloomFilter;

    @Test
    public void bloomTest(){
        boolean flag24 = skuBloomFilter.contains(24L);
        System.out.println(flag24);
        boolean flag10 = skuBloomFilter.contains(10L);
        System.out.println(flag10);
    }
}
