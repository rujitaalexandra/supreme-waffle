package com.sporty.settlement.controller.response;

public record BetResponse(String betId, String userId, String eventId, String eventMarketId, String eventWinnerId, String betAmount) {
}
