# Twitter Bot Interaction Engine

This project is a Spring Boot backend that simulates a Twitter-like interaction system with:
- Human users and bots
- Posts and comments
- Virality scoring
- Anti-spam/anti-abuse caps for bot interactions
- Notification throttling and scheduled notification batching

It uses:
- PostgreSQL for relational data
- Redis for counters, cooldown locks, and notification queues

## Approach Summary

The implementation is split into 3 phases:

1. Phase 1: Core posting/comment flows and persistence  
Users and bots can create posts and comments. Entities are stored in PostgreSQL.

2. Phase 2: Concurrency controls and bot caps (Atomic Locks)  
Redis atomic operations enforce cooldowns and interaction caps under concurrent requests.

3. Phase 3: Notification engine with batching  
Bot notifications are throttled with a 15-minute cooldown and queued in Redis. A scheduled sweeper sends summarized notifications to logs every 5 minutes (test interval).

## Thread Safety in Phase 2 (Atomic Locks)

Phase 2 thread safety is guaranteed using Redis atomic commands, not JVM locks.

### 1) Per bot-human cooldown lock is atomic
- Implemented in `RedisService.acquireCooldown(...)`
- Uses `SET NX EX` semantics via `setIfAbsent(key, value, duration)`
- Key format: `cooldown:bot_{botId}:human_{humanId}`
- Guarantee: only one concurrent request can acquire the lock for a bot-human pair during the cooldown window

Why this is thread-safe:
- Redis executes each command atomically on the server.
- Competing requests from multiple app threads/instances race at Redis, and only one wins.

### 2) Horizontal cap counter is atomic
- Implemented with `INCR` via `incrementBotCount(postId)`
- Key format: `post:{postId}:bot_count`
- Each increment is atomic in Redis, so no lost updates under concurrency.

Flow:
- Increment first
- If value exceeds cap (>100), immediately decrement and reject

### 3) Fail-safe rollback for partial failures
Inside `CommentService.createComment(...)`:
- If cooldown lock was acquired but later validation/persistence fails, cooldown is released.
- If bot count was incremented but comment save fails, counter is decremented.

This compensating rollback keeps Redis state consistent with DB outcomes.

### 4) Multi-instance safety
Because coordination is done in Redis (shared external state), behavior is safe even if the app is horizontally scaled and multiple app nodes process requests at the same time.

## Phase 3 Notification Engine

When a bot interacts with a user-owned post:

- If no recent notification cooldown exists:
  - Immediate log: `Push Notification Sent to User ...`
  - 15-minute cooldown key is set atomically:
    - `user:{id}:notif_cooldown`

- If cooldown exists:
  - Notification message is queued:
    - Redis list: `user:{id}:pending_notifs`

Scheduled sweeper:
- `@Scheduled(cron = "0 */5 * * * *")` in `NotificationService`
- Finds users with pending lists
- Pops all pending messages per user
- Logs summary:
  - `Summarized Push Notification: Bot X and [N] others interacted with your posts.`
- Clears processed list

Note: notification API endpoints were intentionally removed; notifications are operational logs.

## Tech Stack

- Java + Spring Boot 3
- Spring Data JPA
- Spring Data Redis
- PostgreSQL
- Redis
- Docker / Docker Compose

## Project Structure

- `Controller/` REST endpoints
- `Service/` business logic, Redis atomic operations, scheduler
- `Entity/` JPA models
- `Repository/` JPA repositories
- `Config/RedisConfig.java` Redis template configuration

## API Endpoints

### Health
- `GET /health-check`
- `GET /api/post/health-check`

### User
- `POST /signin`
- `GET /user-details/{userId}`
- `POST /user/comment`

### Post
- `POST /api/post/human/write`
- `POST /api/post/bot/write`
- `GET /api/post/get/{postId}`

### Bot
- `POST /bot/create`
- `POST /bot/post`
- `POST /bot/comment`
- `GET /bot/{botId}/likes/{postId}`

## Running the Project

## Option A: Docker (recommended)

```bash
docker compose up -d --build
```

Services:
- App: `http://localhost:8080`
- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`

## Option B: Local

Prerequisites:
- JDK 17+
- Maven wrapper
- Running PostgreSQL + Redis with values matching `src/main/resources/application.properties`

Run:

```bash
./mvnw spring-boot:run
```

## Build / Verify

Compile:

```bash
./mvnw -DskipTests compile
```

Run tests:

```bash
./mvnw test
```
