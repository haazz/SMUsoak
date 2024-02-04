package com.smusoak.restapi.services;

import com.smusoak.restapi.dto.TestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {
    private final RedisService redisService;

    public void addRedisData(TestDto request) {
        redisService.setValues(request.getKey(), request.getValue());
    }

    public void deleteRedisData(TestDto request) {
        redisService.deleteByKey(request.getKey());
    }
}
