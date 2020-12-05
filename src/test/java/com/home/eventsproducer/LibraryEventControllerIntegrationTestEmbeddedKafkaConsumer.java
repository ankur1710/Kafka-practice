package com.home.eventsproducer;

import com.home.eventsproducer.domain.Book;
import com.home.eventsproducer.domain.LibraryEvent;
import com.home.eventsproducer.domain.LibraryEventType;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = LibraryEventControllerIntegrationTestEmbeddedKafkaConsumer.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"},partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventControllerIntegrationTestEmbeddedKafkaConsumer {

	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	EmbeddedKafkaBroker embeddedKafkaBroker;

	private Consumer<Integer, String> consumer;

	//create the consumer from the embeddedKafka
	@BeforeEach
	void setup(){
		Map<String,Object> config =
				new HashMap<>(KafkaTestUtils.consumerProps("group1","true",embeddedKafkaBroker));
		consumer = new DefaultKafkaConsumerFactory<>(config, new IntegerDeserializer(),new StringDeserializer()).createConsumer();
		embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
	}

	@AfterEach
	void tearDown(){
	consumer.close();
	}

	//test will wait for 5secs for producer to write in the topic.
	@Test
	@Timeout(5)
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

		System.out.println("hi");
		//when
		ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.exchange("v1/libraryevent", HttpMethod.POST,request,LibraryEvent.class);

		//then
		assertEquals(HttpStatus.CREATED,HttpStatus.CREATED);

		ConsumerRecord<Integer,String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer,"library-events");
		String expected = "{\n" +
				"    \"libraryEventId\": null,\n" +
				"    \"book\": {\n" +
				"        \"bookId\": 1,\n" +
				"        \"bookName\": \"bookname \",\n" +
				"        \"bookAuthor\": \"bookAuthor\"\n" +
				"    },\n" +
				"    \"libraryEventType\": \"NEW\"\n" +
				"}";
		assertEquals(expected,consumerRecord.value());
	}


}
