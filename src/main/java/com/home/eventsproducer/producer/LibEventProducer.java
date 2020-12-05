package com.home.eventsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.eventsproducer.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LibEventProducer {

    private String topic = "library-events";

    @Autowired
    private KafkaTemplate<Integer,String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;


    public void sendLibraryEvent(LibraryEvent libraryEvent){
        try {
            String payload = objectMapper.writeValueAsString(libraryEvent);
            kafkaTemplate.send(topic,payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }


}
