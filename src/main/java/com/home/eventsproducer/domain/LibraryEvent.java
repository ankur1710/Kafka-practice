package com.home.eventsproducer.domain;

import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class LibraryEvent {
    private Integer libraryEventId;
    @NotNull // the test will fail if I add try to add a null book
    //@Valid //
    private Book book;
    private LibraryEventType libraryEventType;
}
