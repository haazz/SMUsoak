package com.smusoak.restapi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    public void setListOps(String key, List<String> values) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        for (String value : values) {
            listOperations.rightPush(key, value);
        }
    }

    public String getListOpsByIndex(String key, long index) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        return (String) listOperations.index(key, index);
    }

    public List<String> getListOps(String key) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        Long len = listOperations.size(key);
        if(len == 0) {
            return null;
        }
        // List<Object>를 List<String>으로 변환
        return listOperations.range(key, 0, len-1)
                .stream()
                .map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());
    }
}