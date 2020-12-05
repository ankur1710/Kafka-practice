package com.home.eventsproducer.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LibraryEvent {
    private Integer libraryEventId;
    private Book book;
    private LibraryEventType libraryEventType;
}
