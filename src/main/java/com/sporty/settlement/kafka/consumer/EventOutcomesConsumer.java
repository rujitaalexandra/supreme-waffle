package com.sporty.settlement.kafka.consumer;

import com.sporty.settlement.domain.EventOutcome;
import com.sporty.settlement.rocketmq.producer.BetSettlementPublisher;
import com.sporty.settlement.service.BetService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventOutcomesConsumer {

    private final BetService betService;
    private final BetSettlementPublisher betSettlementPublisher;

    public EventOutcomesConsumer(BetService betService, BetSettlementPublisher betSettlementPublisher) {
        this.betService = betService;
        this.betSettlementPublisher = betSettlementPublisher;
    }

    @KafkaListener(topics = "${topics.event.outcomes}")
    public void consume(final EventOutcome eventOutcome) {
        // if there is no bet in the DB at the time we receive the message, then it will not be settled
        // if findAll returns empty we can save the event in a DB and retry the settlement later with a scheduler
        // or maybe the event outcome means the event is finished, so we will not receive any bets for it
        betService.findAllBy(eventOutcome.eventID(), eventOutcome.eventWinnerID())
                .forEach(betSettlementPublisher::publish);
    }
}