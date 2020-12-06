package com.home.eventsproducer.controller;

import com.home.eventsproducer.domain.LibraryEvent;
import com.home.eventsproducer.domain.LibraryEventType;
import com.home.eventsproducer.producer.LibEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
public class LibraryEventController {

        @Autowired
        LibEventProducer libEventProducer;

        @PostMapping("v1/libraryevent")
        public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent){
            libraryEvent.setLibraryEventType(LibraryEventType.NEW);

            //Invoke the Kafka Producer
            log.info("before send library events");
            libEventProducer.sendLibraryEventAsync(libraryEvent);
            //SendResult<Integer,String> sendResult = libEventProducer.sendLibraryEventSync(libraryEvent);
            //log.info("SendResult is {}: "+ sendResult.toString());
            //libEventProducer.sendLibraryEventProducerRecord(libraryEvent);
            log.info("after send library events");

            return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
        }

         @PutMapping("v1/libraryevent")
         public ResponseEntity<?> putLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent){
              libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);


              //Invoke the Kafka Producer
             if(libraryEvent.getLibraryEventId() == null){
                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("please pass the eventId");
             }
             else {

                 log.info("before send library events");
                 libEventProducer.sendLibraryEventProducerRecord(libraryEvent);
                 log.info("after send library events");

                 return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
             }
        }

}
