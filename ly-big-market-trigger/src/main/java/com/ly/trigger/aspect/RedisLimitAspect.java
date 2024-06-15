package com.ly.trigger.aspect;

import com.ly.trigger.annotation.RedisLimit;
import com.ly.trigger.exception.RedisLimitException;
import com.ly.types.common.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.RedissonScript;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
//@RequiredArgsConstructor
@Aspect
public class RedisLimitAspect {

    @Autowired
    private RedissonClient redissonService;

    private String script;

    @PostConstruct
    public void preInit() {
        ClassPathResource resource = new ClassPathResource("redisLimit.lua");
        try {
            InputStream input = resource.getInputStream();
            int available = input.available();
            byte[] bytes = new byte[available];
            input.read(bytes);
            script = new String(bytes);
            log.info("redisLimit.lua:{}", script);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @Pointcut("@annotation(com.ly.trigger.annotation.RedisLimit)")
    public void redisLimit() {
    }

    @Before("redisLimit()")
    public void beforeAdvice(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedisLimit redisLimit = method.getAnnotation(RedisLimit.class);
        if (redisLimit != null) {
            //获取redis的key
            String key = redisLimit.key();
            String className = method.getDeclaringClass().getName();
            String name = method.getName();
            log.info("类-{}-中的-{}-方法进行被限流", className, name);
            String limitKey = key + Constants.COLON +
                    className + Constants.COLON + method.getName();

            log.info(limitKey);

            if (StringUtils.isEmpty(key)) {
                throw new RedisLimitException("key cannot be null");
            }

            Long limit = redisLimit.permitsPerSecond();

            Long expire = redisLimit.expire();


            Long count = redissonService
                    .getScript()
                    .eval(RScript.Mode.READ_WRITE, script, RScript.ReturnType.VALUE,
                            Collections.singletonList(key),
                            limit,
                            expire);

            log.info("Access try count is {} for key={}", count, key);

            if (count != null && count == 0) {
                log.debug("获取key失败，key为{}", key);
                throw new RedisLimitException(redisLimit.msg());
            }

        }

    }
}