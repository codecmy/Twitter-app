package com.example.Twitter.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void incrementViralityScore(String postId, long delta) {
        String key = "post:" + postId + ":virality_score";
        Long updated = redisTemplate.opsForValue().increment(key, delta);
        if (updated == null) {
            throw new IllegalStateException("Unable to update virality score");
        }
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

    private static final Duration NOTIFICATION_COOLDOWN = Duration.ofMinutes(15);

    public boolean tryAcquireNotificationCooldown(String userId) {
        String key = "user:" + userId + ":notif_cooldown";
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, 1, NOTIFICATION_COOLDOWN);
        return Boolean.TRUE.equals(acquired);
    }

    public void addPendingNotification(String userId, String message) {
        String key = "user:" + userId + ":pending_notifs";
        redisTemplate.opsForList().rightPush(key, message);
    }

    public List<Object> getPendingNotifications(String userId) {
        String key = "user:" + userId + ":pending_notifs";
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public Set<String> getUserIdsWithPendingNotifications() {
        Set<String> keys = redisTemplate.keys("user:*:pending_notifs");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> userIds = new HashSet<>();
        for (String key : keys) {
            Long size = redisTemplate.opsForList().size(key);
            if (size == null || size <= 0) {
                continue;
            }
            String userId = extractUserIdFromPendingKey(key);
            if (userId != null) {
                userIds.add(userId);
            }
        }
        return userIds;
    }

    public List<Object> popAllPendingNotifications(String userId) {
        String key = "user:" + userId + ":pending_notifs";
        List<Object> popped = new java.util.ArrayList<>();
        while (true) {
            Object message = redisTemplate.opsForList().leftPop(key);
            if (message == null) {
                break;
            }
            popped.add(message);
        }
        redisTemplate.delete(key);
        return popped;
    }

    public void clearPendingNotifications(String userId) {
        String key = "user:" + userId + ":pending_notifs";
        redisTemplate.delete(key);
    }

    private String extractUserIdFromPendingKey(String key) {
        String prefix = "user:";
        String suffix = ":pending_notifs";
        if (!key.startsWith(prefix) || !key.endsWith(suffix)) {
            return null;
        }
        return key.substring(prefix.length(), key.length() - suffix.length());
    }
}
