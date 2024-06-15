package com.ly.trigger.annotation;

import java.lang.annotation.*;

/**
 * redis限流自定义注解
 * @author zyw
 */
//注解的保留位置,RUNTIME表示这种类型的Annotations将被JVM保留,所以他们能在运行时被JVM或其他使用反射机制的代码所读取和使用。
@Retention(RetentionPolicy.RUNTIME)
//说明注解的作用目标，METHOD表示用来修饰方法
@Target({ElementType.METHOD})
//说明该注解将被包含在javadoc中
@Documented
public @interface RedisLimit {
    /**
     * 资源的key,唯一
     * 作用：不同的接口，不同的流量控制
     */
    String key() default "";

    /**
     * 最多的访问限制次数
     */
    long permitsPerSecond() default 2;

    /**
     * 过期时间也可以理解为单位时间，单位秒，默认60
     */
    long expire() default 60;


    /**
     * 得不到令牌的提示语
     */
    String msg() default "系统繁忙,请稍后再试.......";
}
