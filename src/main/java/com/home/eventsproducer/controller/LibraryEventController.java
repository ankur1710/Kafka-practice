package com.home.eventsproducer.controller;

import com.home.eventsproducer.domain.LibraryEvent;
import com.home.eventsproducer.producer.LibEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LibraryEventController {

        @Autowired
        LibEventProducer libEventProducer;

        @PostMapping("v1/libraryevent")
        public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent){
            //Invoke the Kafka Producer
            log.info("before send library events");
            libEventProducer.sendLibraryEvent(libraryEvent);
            log.info("after send library events");

            return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
        }

}
