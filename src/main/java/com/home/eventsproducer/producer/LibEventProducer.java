package com.home.eventsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.eventsproducer.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class LibEventProducer {

    private String topic = "library-events";

    @Autowired
    private KafkaTemplate<Integer,String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;


    public void sendLibraryEventAsync(LibraryEvent libraryEvent){
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

    public SendResult<Integer,String> sendLibraryEventSync(LibraryEvent libraryEvent){
        SendResult<Integer,String> sendResult = null;
        try {
            Integer key  = libraryEvent.getLibraryEventId();
            String value = objectMapper.writeValueAsString(libraryEvent);
            sendResult =  kafkaTemplate.send(topic,key,value).get();

        } catch (JsonProcessingException | InterruptedException |ExecutionException e) {
            log.error("error sending the message and the exception is: {} "+e.getMessage());
        }
        return sendResult;
    }


    public void sendLibraryEventProducerRecord(LibraryEvent libraryEvent){
        try {
            Integer key  = libraryEvent.getLibraryEventId();
            String value = objectMapper.writeValueAsString(libraryEvent);
            ProducerRecord producerRecord = buildProducerRecord(topic,key,value);
            ListenableFuture<SendResult<Integer,String>> listenableFuture =
                    kafkaTemplate.send(producerRecord);
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

    private ProducerRecord buildProducerRecord(String topic, Integer key, String value) {
        List headers = new ArrayList();
        headers.add("Content-Type");
        return new ProducerRecord(topic,1,key,value,null);
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
