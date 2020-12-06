package com.home.eventconsumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.home.eventconsumer.config.LibraryEventConsumerConfig;
import com.home.eventconsumer.consumer.LibEventsConsumer;
import com.home.eventconsumer.entity.LibraryEvent;
import com.home.eventconsumer.service.LibraryEventService;
import net.bytebuddy.asm.Advice;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(topics = {"library-events"},partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
		                          "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"})
class EventConsumerIntegrationTestsEmbeddedKafka {

	@Autowired
	EmbeddedKafkaBroker embeddedKafkaBroker;

	@Autowired
	KafkaTemplate<Integer, String> kafkaTemplate;

	@Autowired
	KafkaListenerEndpointRegistry endpointRegistry; // it has access to the listener containers

	@SpyBean
	LibEventsConsumer libEventsConsumerSpy;

	@SpyBean // this will give you the actual bean
	LibraryEventService libraryEventServiceSpy;

	@BeforeEach
	void setup(){
		//get all the containers from the brokers.
		for(MessageListenerContainer container : endpointRegistry.getListenerContainers()){
			ContainerTestUtils.waitForAssignment(container,embeddedKafkaBroker.getPartitionsPerTopic());
		}
	}



	@Test
	void publishNewLibEvent() throws ExecutionException, InterruptedException, JsonProcessingException {
		//given
		String jsonMessage = "{\n" +
				"    \"libraryEventId\": null,\n" +
				"    \"book\": {\n" +
				"        \"bookId\": 596,\n" +
				"        \"bookName\": \"unix \",\n" +
				"        \"bookAuthor\": \"kanetkar\"\n" +
				"    },\n" +
				"    \"libraryEventType\": \"NEW\"\n" +
				"}";

		kafkaTemplate.send("library-events",jsonMessage).get();

		//when
		CountDownLatch latch = new CountDownLatch(1);
		latch.await(3, TimeUnit.SECONDS);

		//then
		// here we are verifying that onMessage method from the libEventsConsumerSpy is invocked once or not
		verify(libEventsConsumerSpy,times(1)).onMessage(isA(ConsumerRecord.class));
		verify(libraryEventServiceSpy,times(1)).processLibraryEvent(isA(ConsumerRecord.class));
	}

}
