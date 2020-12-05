package com.home.eventsproducer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.eventsproducer.controller.LibraryEventController;
import com.home.eventsproducer.domain.Book;
import com.home.eventsproducer.domain.LibraryEvent;
import com.home.eventsproducer.domain.LibraryEventType;
import com.home.eventsproducer.producer.LibEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(LibraryEventController.class)
@AutoConfigureMockMvc
public class LibraryEventControllerUnitTest {

    // it has access to all the endpoints
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LibEventProducer libEventProducer;

    @Test
    void postLibraryEvent() throws Exception {
        //given
        Book book = Book.builder()
                .bookId(1)
                .bookName("bookname")
                .bookAuthor("bookAuthor")
                .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .libraryEventType(LibraryEventType.NEW)
                .build();

        //this is added to simulate the actual producer method call asynchronous
        doNothing().when(libEventProducer).sendLibraryEventProducerRecord(isA(LibraryEvent.class));

        //when
        mockMvc.perform(post("/v1/libraryevent")
                .content(objectMapper.writeValueAsString(libraryEvent))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()); // this the test case

    }

    @Test
    void postLibraryEvent_4xxError() throws Exception {
        //given
        Book book = Book.builder()
                .bookId(1)
                .bookName(null)
                .bookAuthor(null)
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(null)
                .libraryEventType(LibraryEventType.NEW)
                .build();

        //this is added to simulate the actual producer method call asynchronous
        doNothing().when(libEventProducer).sendLibraryEventProducerRecord(isA(LibraryEvent.class));
        String expectedErrorMessage = "book, must not be null";

        //when
        mockMvc.perform(post("/v1/libraryevent")
                .content(objectMapper.writeValueAsString(libraryEvent))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedErrorMessage));

    }



}
