package com.home.eventsproducer;

import com.home.eventsproducer.domain.Book;
import com.home.eventsproducer.domain.LibraryEvent;
import com.home.eventsproducer.domain.LibraryEventType;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EmbeddedKafka(topics = {"library-events"},partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventControllerIntegrationTestEmbeddedKafkaPut {

	@Autowired
	TestRestTemplate testRestTemplate; //= new TestRestTemplate();

	@Test
	public void putLibraryEvent(){

		//given
		Book book = Book.builder()
				.bookId(1)
				.bookName("bookname")
				.bookAuthor("bookAuthor")
				.build();
		LibraryEvent libraryEvent = LibraryEvent.builder()
				.libraryEventId(111)
				.book(book)
				.libraryEventType(LibraryEventType.UPDATE)
				.build();
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", MediaType.APPLICATION_JSON.toString());
		HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent,headers);

		//when
		ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.exchange("http://localhost:8081/v1/libraryevent", HttpMethod.PUT,request,LibraryEvent.class);

		//then
		Assert.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
	}

	@Test
	public void putLibraryEventWithNull(){

		//given
		Book book = Book.builder()
				.bookId(1)
				.bookName("bookname")
				.bookAuthor("bookAuthor")
				.build();
		LibraryEvent libraryEvent = LibraryEvent.builder()
				.libraryEventId(null)
				.book(book)
				.libraryEventType(LibraryEventType.UPDATE)
				.build();
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", MediaType.APPLICATION_JSON.toString());
		HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent,headers);

		//when
		ResponseEntity<?> responseEntity = testRestTemplate.exchange("http://localhost:8081/v1/libraryevent", HttpMethod.PUT,request,LibraryEvent.class);

		//then
		Assert.assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode());
		//Assert.assertEquals("please pass the eventId",responseEntity.getBody().toString());
	}


}
