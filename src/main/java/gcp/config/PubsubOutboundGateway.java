package gcp.config;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.stereotype.Component;

import gcp.model.dto.Person;

//tag::messageGateway[]
@MessagingGateway(defaultRequestChannel = "pubSubOutputChannel")
@Component
public interface PubsubOutboundGateway {
	void sendPersonToPubSub(Person person);
}
// end::messageGateway[]
