package com.home.eventsproducer.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Book {
    private Integer bookId;
    private String bookName;
    private String bookAuthor;
}
