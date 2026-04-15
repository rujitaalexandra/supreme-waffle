package com.sporty.settlement.service;

import com.sporty.settlement.domain.Bet;
import com.sporty.settlement.repository.BetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BetService {

    private final BetRepository betRepository;

    public BetService(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    public void save(Bet bet) {
        betRepository.save(bet);
    }

    public List<Bet> findAllBy(String eventId, String eventWinnerId) {
        return betRepository.findByEventIdAndEventWinnerId(eventId, eventWinnerId);
    }

    public List<Bet> findAll() {
        return betRepository.findAll();
    }
}
