package com.home.eventsproducer;

import com.home.eventsproducer.domain.Book;
import com.home.eventsproducer.domain.LibraryEvent;
import com.home.eventsproducer.domain.LibraryEventType;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
public class LibraryEventControllerIntegrationTest {


//	TestRestTemplate testRestTemplate = new TestRestTemplate();

	@Autowired
	TestRestTemplate testRestTemplate;

	@Test
	public void postLibraryEvent(){

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
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", MediaType.APPLICATION_JSON.toString());
		HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent,headers);


		//when
		ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.exchange("http://localhost:8081/v1/libraryevent", HttpMethod.POST,request,LibraryEvent.class);

		//then
		Assert.assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
	}


}
