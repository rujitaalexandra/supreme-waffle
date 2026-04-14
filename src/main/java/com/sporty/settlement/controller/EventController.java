package com.sporty.settlement.controller;

import com.sporty.settlement.controller.request.PublishEventOutcomeRequest;
import com.sporty.settlement.domain.EventOutcome;
import com.sporty.settlement.kafka.producer.EventOutcomeProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {

    private final EventOutcomeProducer eventOutcomeProducer;

    public EventController(EventOutcomeProducer eventOutcomeProducer) {
        this.eventOutcomeProducer = eventOutcomeProducer;
    }

    @PostMapping("/publish-event-outcome")
    public ResponseEntity<String> publishEventOutcome(@RequestBody PublishEventOutcomeRequest request) {
        eventOutcomeProducer.publish(new EventOutcome(request.eventId(), request.eventName(), request.eventWinnerId()));
        return ResponseEntity.ok("Event outcome published");
    }
}
