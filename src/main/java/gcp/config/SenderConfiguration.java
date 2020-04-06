package gcp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.cloud.gcp.pubsub.support.converter.JacksonPubSubMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration for sending custom JSON payloads to a Pub/Sub topic.
 *
 * @author Farith
 */
@Configuration
@Slf4j
public class SenderConfiguration {
	@Value("${app.subs.topicName}")
	private String topicName;

	@Bean
	public DirectChannel pubSubOutputChannel() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "pubSubOutputChannel")
	public MessageHandler messageSender(PubSubTemplate pubSubTemplate) {
		PubSubMessageHandler adapter = new PubSubMessageHandler(pubSubTemplate, topicName);
		adapter.setPublishCallback(new ListenableFutureCallback<String>() {
			@Override
			public void onFailure(Throwable ex) {
				log.info("There was an error sending the message.");
			}

			@Override
			public void onSuccess(String result) {
				log.info("Message was sent successfully. Result: {}", result);
			}
		});

		return adapter;
	}
	
	/**
	 * This bean enables serialization/deserialization of Java objects to JSON allowing you
	 * utilize JSON message payloads in Cloud Pub/Sub.
	 * @param objectMapper the object mapper to use
	 * @return a Jackson message converter
	 */
	@Bean
	public JacksonPubSubMessageConverter jacksonPubSubMessageConverter(ObjectMapper objectMapper) {
		return new JacksonPubSubMessageConverter(objectMapper);
	}
}