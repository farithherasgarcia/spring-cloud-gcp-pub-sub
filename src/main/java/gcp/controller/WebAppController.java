package gcp.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gcp.pubsub.support.PublisherFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import gcp.config.PubsubOutboundGateway;
import gcp.model.dto.Person;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class WebAppController {

	@Autowired
	@Qualifier("ProcessedPersonsList")
	private ArrayList<Person> processedPersonsList;

	PublisherFactory publisher;
	
	// tag::autowireGateway[]
	@Autowired
	private PubsubOutboundGateway messagingGateway;
	// end::autowireGateway[]
	
	@PostMapping("/createPerson")
	public RedirectView createUser(@RequestParam("name") String name, @RequestParam("age") int age) {
		Person person = new Person(name, age);
		log.info("Ready to send Person: {}" , person);
		
		this.messagingGateway.sendPersonToPubSub(person);
		
		log.info("Person sent!!");
		return new RedirectView("/");
	}

	@GetMapping("/listPeople")
	public List<Person> listPeople() {
		return this.processedPersonsList;
	}
}
