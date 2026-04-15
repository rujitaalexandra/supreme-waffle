package com.sporty.settlement.controller;

import com.sporty.settlement.controller.request.AddBetRequest;
import com.sporty.settlement.controller.response.BetResponse;
import com.sporty.settlement.domain.Bet;
import com.sporty.settlement.service.BetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bets")
public class BetController {

    private final BetService betService;

    public BetController(BetService betService) {
        this.betService = betService;
    }

    @PutMapping
    public ResponseEntity<Void> addBet(@RequestBody AddBetRequest request) {
        betService.save(new Bet(request.betId(), request.userId(), request.eventId(),
                request.eventMarketId(), request.eventWinnerId(), request.betAmount()));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<BetResponse>> getAllBets() {
        List<BetResponse> bets = betService.findAll().stream()
                .map(bet -> new BetResponse(bet.betId(), bet.userId(), bet.eventId(),
                        bet.eventMarketId(), bet.eventWinnerId(), bet.betAmount()))
                .toList();
        return ResponseEntity.ok(bets);
    }
}
