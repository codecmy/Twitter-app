package com.example.Twitter.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public long incrementViralityScore(String postId, long delta) {
        String key = "post:" + postId + ":virality_score";
        Long updated = redisTemplate.opsForValue().increment(key, delta);
        if (updated == null) {
            throw new IllegalStateException("Unable to update virality score");
        }
        return updated;
    }

    public long incrementBotCount(String postId) {
        String key = "post:" + postId + ":bot_count";
        Long updated = redisTemplate.opsForValue().increment(key);
        if (updated == null) {
            throw new IllegalStateException("Unable to update bot count");
        }
        return updated;
    }

    public void decrementBotCount(String postId) {
        String key = "post:" + postId + ":bot_count";
        redisTemplate.opsForValue().decrement(key);
    }

    public boolean acquireCooldown(String botId, String humanId, Duration duration) {
        String key = cooldownKey(botId, humanId);
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, 1, duration);
        return Boolean.TRUE.equals(acquired);
    }

    public void releaseCooldown(String botId, String humanId) {
        String key = cooldownKey(botId, humanId);
        redisTemplate.delete(key);
    }

    private String cooldownKey(String botId, String humanId) {
        return "cooldown:bot_" + botId + ":human_" + humanId;
    }
}
