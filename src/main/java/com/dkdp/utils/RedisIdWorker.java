package com.dkdp.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 全局唯一id
 */
@Component
public class RedisIdWorker {
    private StringRedisTemplate stringRedisTemplate;

    public RedisIdWorker(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public  long nextId(String keyPrefix){
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        // redis自增长
        Long count = stringRedisTemplate.opsForValue().increment("irc:" + keyPrefix + ":" + date);
        return  nowSecond<< 32 | count;
    }
}
