package com.sporty.settlement.rocketmq.consumer;

import com.sporty.settlement.domain.Bet;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(topic = "${topics.bet.settlements}", consumerGroup = "${topics.bet.consumer-group}")
public class BetSettlementConsumer implements RocketMQListener<Bet> {

    private static final Logger logger = LoggerFactory.getLogger(BetSettlementConsumer.class);

    @Override
    public void onMessage(Bet bet) {
        logger.info("Received bet settlement from RocketMQ: {}", bet);
    }
}
