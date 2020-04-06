package gcp.config;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;

import gcp.model.dto.Person;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration for receiving and processing messages from a Pub/Sub topic.
 *
 * @author Farith
 */
@Configuration
@Slf4j
public class ReceiverConfiguration {
	@Value("${app.pubs.subscriptionName}")
	private String subscriptionName;

	private final ArrayList<Person> processedPersonsList = new ArrayList<>();

	@Bean
	public DirectChannel pubSubInputChannel() {
		return new DirectChannel();
	}

	@Bean
	public PubSubInboundChannelAdapter messageChannelAdapter(
			@Qualifier("pubSubInputChannel") MessageChannel inputChannel,
			PubSubTemplate pubSubTemplate) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionName);
		adapter.setOutputChannel(inputChannel);
		adapter.setAckMode(AckMode.MANUAL);
		adapter.setPayloadType(Person.class);
		return adapter;
	}

	@ServiceActivator(inputChannel = "pubSubInputChannel")
	public void messageReceiver(Person payload,
			@Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
		log.info("Message arrived! Payload: " + payload);
		this.processedPersonsList.add(payload);
		message.ack();
	}

	@Bean
	@Qualifier("ProcessedPersonsList")
	public ArrayList<Person> processedPersonsList() {
		return this.processedPersonsList;
	}
}
