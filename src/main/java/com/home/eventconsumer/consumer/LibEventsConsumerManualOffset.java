package com.home.eventconsumer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LibEventsConsumerManualOffset implements AcknowledgingMessageListener<Integer,String> {

//    @KafkaListener (topics = {"library-events"})
//    public void onMessage(ConsumerRecord<Integer,String> consumerRecord){
//        log.info("onMessage record : {} "+ consumerRecord);
//    }

    @Override
    @KafkaListener (topics = {"library-events"})
    public void onMessage(ConsumerRecord<Integer, String> data, Acknowledgment acknowledgment) {
        log.info("onMessage record : {} "+ data);
        acknowledgment.acknowledge(); // this lets the message listener that message has been acknowledged.

    }
}
