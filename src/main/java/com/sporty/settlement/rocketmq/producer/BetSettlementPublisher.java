package com.sporty.settlement.rocketmq.producer;

import com.sporty.settlement.domain.Bet;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

@Service
public class BetSettlementPublisher {

    static final String TOPIC = "bet-settlements";

    private final RocketMQTemplate rocketMQTemplate;

    public BetSettlementPublisher(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    public void publish(final Bet bet) {
        rocketMQTemplate.convertAndSend(TOPIC, bet);
    }
}
