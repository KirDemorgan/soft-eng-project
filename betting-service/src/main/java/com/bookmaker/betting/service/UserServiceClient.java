package com.bookmaker.betting.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @PutMapping("/api/users/{userId}/balance")
    Map<String, Object> updateBalance(@PathVariable("userId") Long userId, @RequestBody Map<String, BigDecimal> request);
    
    default void updateBalance(Long userId, BigDecimal amount) {
        updateBalance(userId, Map.of("amount", amount));
    }
}