package com.example.Twitter.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class NotificationService {
    private static final Pattern BOT_REPLY_PATTERN =
            Pattern.compile("^Bot\\s+(.+?)\\s+replied\\s+to\\s+your\\s+post.*$", Pattern.CASE_INSENSITIVE);

    @Autowired
    private RedisService redisService;
    public void handleBotInteraction(String userId, String message) {
        if (redisService.tryAcquireNotificationCooldown(userId)) {
            sendImmediateNotification(userId, message);
        } else {
            queueNotification(userId, message);
        }
    }

    public void sendImmediateNotification(String userId, String message) {
        log.info("Push Notification Sent to User {}: {}", userId, message);
    }

    public void queueNotification(String userId, String message) {
        redisService.addPendingNotification(userId, message);
    }

    public List<Object> getPendingNotifications(String userId) {
        return redisService.getPendingNotifications(userId);
    }

    public void clearPendingNotifications(String userId) {
        redisService.clearPendingNotifications(userId);
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void sweepPendingNotifications() {
        Set<String> userIds = redisService.getUserIdsWithPendingNotifications();
        for (String userId : userIds) {
            List<Object> messages = redisService.popAllPendingNotifications(userId);
            if (messages == null || messages.isEmpty()) {
                continue;
            }

            String firstMessage = String.valueOf(messages.get(0));
            String leadBot = extractLeadBot(firstMessage);
            int othersCount = Math.max(0, messages.size() - 1);
            System.out.println("Summarized Push Notification: " + leadBot + " and [" + othersCount + "] others interacted with your posts.");
        }
    }

    private String extractLeadBot(String message) {
        Matcher matcher = BOT_REPLY_PATTERN.matcher(message);
        if (matcher.matches()) {
            return "Bot " + matcher.group(1).trim();
        }
        return "Bot X";
    }
}
