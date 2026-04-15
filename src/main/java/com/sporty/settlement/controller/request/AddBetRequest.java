package com.sporty.settlement.controller.request;

public record AddBetRequest(String betId, String userId, String eventId, String eventMarketId, String eventWinnerId, String betAmount) {
}
