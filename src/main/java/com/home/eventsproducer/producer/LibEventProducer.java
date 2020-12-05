package com.home.eventsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.eventsproducer.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

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
            Integer key  = libraryEvent.getLibraryEventId();
            String value = objectMapper.writeValueAsString(libraryEvent);
           ListenableFuture<SendResult<Integer,String>> listenableFuture =
                   kafkaTemplate.send(topic,key,value);
           listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
               @Override
               public void onFailure(Throwable throwable) {
                   handleFailure(key,value,throwable);
               }

               @Override
               public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key,value,result);
               }
           });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }

    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("error sending the message and the exception is: {} "+ex.getMessage());
        try{
            throw ex;
        } catch (Throwable throwable) {
           log.error("on failure: {} "+ throwable.getMessage());
        }
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("message sent successfully for the key: {} and the value : {} , partition: {}"
                + key+", "+value+", "+result.getRecordMetadata().partition());
    }


}
