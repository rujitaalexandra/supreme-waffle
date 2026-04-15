package com.sporty.settlement.rocketmq.producer;

import com.sporty.settlement.domain.Bet;
import com.sporty.settlement.kafka.producer.EventOutcomeProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BetSettlementPublisher {
    private static final Logger logger = LoggerFactory.getLogger(BetSettlementPublisher.class);

    private final RocketMQTemplate rocketMQTemplate;
    @Value("${topics.bet.settlements}")
    private String topic;

    public BetSettlementPublisher(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    public void publish(final Bet bet) {
//        rocketMQTemplate.convertAndSend(TOPIC, bet);

        SendResult result = rocketMQTemplate.syncSend(topic, bet);
        logger.info("Sent bet {} to RocketMQ, status: {}", bet.betId(), result.getSendStatus());
    }
}
