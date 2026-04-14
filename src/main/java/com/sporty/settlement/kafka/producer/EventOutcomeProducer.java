package com.sporty.settlement.kafka.producer;

import com.sporty.settlement.domain.EventOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventOutcomeProducer {
    private static final Logger logger = LoggerFactory.getLogger(EventOutcomeProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventOutcomeProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(final EventOutcome eventOutcome) {
        try {
            kafkaTemplate.send("event-outcomes",
                    eventOutcome.eventID(),
                    eventOutcome);
        } catch (Exception e) {
            logger.error("Error publishing event outcome", e);
        }
    }
}
