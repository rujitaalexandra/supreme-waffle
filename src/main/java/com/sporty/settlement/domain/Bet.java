package com.sporty.settlement.domain;

public record Bet(String betId, String userId, String eventId, String eventMarketId, String eventWinnerId, String betAmount) {
}
