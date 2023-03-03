package com.atguigu.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//该注解只能用在哪些地方
@Target({ElementType.TYPE,ElementType.METHOD})
//该注解的生命周期
@Retention(RetentionPolicy.RUNTIME)
public @interface ShopCache {
    //定义一个属性值
    String prefix() default "cache";

    //是否开启布隆过滤器
    boolean enableBloom() default false;
}
