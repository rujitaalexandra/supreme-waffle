package com.sporty.settlement.kafka.producer;

import com.sporty.settlement.domain.EventOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EventOutcomeProducer {
    private static final Logger logger = LoggerFactory.getLogger(EventOutcomeProducer.class);

    @Value("${topics.event.outcomes}")
    private String topic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventOutcomeProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, Object>> publish(final EventOutcome eventOutcome) throws InterruptedException {
        return kafkaTemplate.send(topic, eventOutcome.eventID(), eventOutcome)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        logger.error("Failed to publish event outcome for eventId={}: {}",
                                eventOutcome.eventID(), ex.getMessage(), ex);
                    } else {
                        logger.info("Published event outcome for eventId={}, offset={}",
                                eventOutcome.eventID(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
