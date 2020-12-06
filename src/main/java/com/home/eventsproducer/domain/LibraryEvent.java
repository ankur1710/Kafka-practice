package com.home.eventsproducer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryEvent {
    private Integer libraryEventId;
    @NotNull // the test will fail if I add try to add a null book
    @Valid //
    private Book book;
    private LibraryEventType libraryEventType;
}
