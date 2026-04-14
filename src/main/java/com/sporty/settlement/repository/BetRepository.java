package com.sporty.settlement.repository;

import com.sporty.settlement.domain.Bet;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

@Repository
public class BetRepository {

    private static final String KEY_PREFIX = "bets:event:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonMapper jsonMapper;

    public BetRepository(RedisTemplate<String, Object> redisTemplate, JsonMapper jsonMapper) {
        this.redisTemplate = redisTemplate;
        this.jsonMapper = jsonMapper;
    }

    public void save(Bet bet) {
        String key = KEY_PREFIX + bet.eventId();
        redisTemplate.opsForHash().put(key, bet.betId(), bet);
    }

    public List<Bet> findByEventIdAndEventWinnerId(String eventId, String eventWinnerId) {
        String key = KEY_PREFIX + eventId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        return entries.values().stream()
                .map(value -> jsonMapper.convertValue(value, Bet.class))
                .filter(bet -> eventWinnerId.equals(bet.eventWinnerId()))
                .toList();
    }
}