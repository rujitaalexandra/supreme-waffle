package com.sporty.settlement.controller;

import com.sporty.settlement.controller.request.PublishEventOutcomeRequest;
import com.sporty.settlement.domain.EventOutcome;
import com.sporty.settlement.kafka.producer.EventOutcomeProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventOutcomeProducer eventOutcomeProducer;

    public EventController(EventOutcomeProducer eventOutcomeProducer) {
        this.eventOutcomeProducer = eventOutcomeProducer;
    }

    @PostMapping("/publish-event-outcome")
    public CompletableFuture<ResponseEntity<String>> publishEventOutcome(@RequestBody PublishEventOutcomeRequest request) throws InterruptedException {
        return eventOutcomeProducer.publish(new EventOutcome(request.eventId(), request.eventName(), request.eventWinnerId()))
                .thenApply(result -> ResponseEntity.accepted().body("Event outcome published"))
                .exceptionally(ex -> ResponseEntity.status(502).body("Failed to publish event outcome: " + ex.getCause().getMessage()));
    }
}
