package com.home.eventconsumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.eventconsumer.entity.LibraryEvent;
import com.home.eventconsumer.jpa.LibraryEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class LibraryEventService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private LibraryEventRepository libraryEventRepository;

    public void processLibraryEvent(ConsumerRecord<Integer,String> consumerRecord) throws JsonProcessingException {
        LibraryEvent libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
        log.info("library event: {} "+ libraryEvent);

        switch(libraryEvent.getLibraryEventType()){
            case NEW:
                save(libraryEvent);
                break;
            case UPDATE:
                validate(libraryEvent);
                save(libraryEvent);
                break;
            default:
                log.info("invalid Library Event");
        }
    }

    private void validate(LibraryEvent libraryEvent) {
        if(libraryEvent.getLibraryEventId() == null){
            throw new IllegalArgumentException("Library event id is missing");
        }
        Optional<LibraryEvent> libraryEventOptional =
                libraryEventRepository.findById(libraryEvent.getLibraryEventId());
        if(!libraryEventOptional.isPresent()){
            throw new IllegalArgumentException("No Such Library Event present with the id: {}"+ libraryEvent.getLibraryEventId());
        }
        log.info("Library Event Successfully validated");
    }

    private void save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent); // this is to create the mapping
        libraryEventRepository.save(libraryEvent);
        log.info("successfully persisted the library event");
    }
}
