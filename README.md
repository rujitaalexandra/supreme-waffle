# Settlement Service

A Spring Boot microservice that handles bet settlement. It receives event outcomes via a REST API, publishes them to Kafka, queries Redis for matching bets, and forwards settled bets to RocketMQ.

## Architecture

```
POST /events/publish-event-outcome
        │
        ▼
  Kafka (event-outcomes)
        │
        ▼
  EventOutcomesConsumer
        │ queries Redis for bets matching eventId + winnerId
        ▼
  BetSettlementPublisher
        │
        ▼
  RocketMQ (bet-settlements)
```

Bets are stored in Redis independently via `PUT /bets` and can be retrieved via `GET /bets`.

## Prerequisites

- Java 21
- Docker and Docker Compose

## Running

### 1. Start infrastructure

```bash
docker compose up -d
```

This starts:

Kafka on  port `9094`

RocketMQ NameServer on port `9876` 

RocketMQ Broker on port `10911`, `10909` 

Redis runs embedded inside the application on port `6370`.

### 2. Start the application

```bash
./gradlew bootRun
```

The service starts on `http://localhost:8080`.

### 3. Verify RocketMQ broker is registered

```bash
docker exec rocketmq-namesrv sh mqadmin clusterList -n localhost:9876
```

The `#Addr` column should show `127.0.0.1:10911`.

## API

### Store a bet in Redis

```
PUT /bets
```

**Request body:**
```json
{
  "betId": "bet-1",
  "userId": "user-1",
  "eventId": "event-1",
  "eventMarketId": "market-1",
  "eventWinnerId": "winner-1",
  "betAmount": "100"
}
```

**Responses:**
- `200 OK` — bet stored

---

### Get all bets from Redis

```
GET /bets
```

**Response body:**
```json
[
  {
    "betId": "bet-1",
    "userId": "user-1",
    "eventId": "event-1",
    "eventMarketId": "market-1",
    "eventWinnerId": "winner-1",
    "betAmount": "100"
  }
]
```

---

### Publish an event outcome

Triggers settlement for all bets in Redis that match the `eventId` and `eventWinnerId`.

```
POST /events/publish-event-outcome
```

**Request body:**
```json
{
  "eventId": "event-1",
  "eventName": "Final Match",
  "eventWinnerId": "winner-1"
}
```

**Responses:**
- `202 Accepted` — event outcome published to Kafka successfully
- `502 Bad Gateway` — failed to reach Kafka broker

## Configuration

All configuration is in `src/main/resources/application.yml`.

| Property | Default | Description |
|---|---|---|
| `spring.data.redis.port` | `6370` | Embedded Redis port |
| `spring.kafka.bootstrap-servers` | `localhost:9094` | Kafka broker address |
| `rocketmq.name-server` | `localhost:9876` | RocketMQ nameserver address |
| `topics.event.outcomes` | `event-outcomes` | Kafka topic for event outcomes |
| `topics.bet.settlements` | `bet-settlements` | RocketMQ topic for settled bets |
