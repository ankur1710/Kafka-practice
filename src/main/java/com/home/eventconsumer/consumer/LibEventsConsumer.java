package com.home.eventconsumer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LibEventsConsumer {

    @KafkaListener (topics = {"library-events"})
    public void onMessage(ConsumerRecord<Integer,String> consumerRecord){
        log.info("onMessage record : {} "+ consumerRecord);
    }
}
