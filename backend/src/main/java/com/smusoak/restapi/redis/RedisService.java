package com.smusoak.restapi.redis;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@Service
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setValues(String key, String data) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    public void setValues(String key, String data, Duration duration) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    @Transactional(readOnly = true)
    public String getValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if (values.get(key) == null) {
            return "false";
        }
        return (String) values.get(key);
    }

    public void deleteByKey(String key) {
        redisTemplate.delete(key);
    }

    public void setExpire(String key, long ttl) {
        redisTemplate.expire(key, ttl, TimeUnit.MILLISECONDS);
    }

    public void setListOps(String key, String... values) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        for (String value : values) {
            listOperations.rightPush(key, value);
        }
    }

    public String getListOpsByIndex(String key, long index) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        return (String) listOperations.index(key, index);
    }
}
