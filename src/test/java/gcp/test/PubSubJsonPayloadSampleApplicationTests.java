package gcp.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;

import java.util.List;
import java.util.Map;

import org.awaitility.Duration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.gcp.core.util.MapBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import gcp.PubSubApplication;
import gcp.model.dto.Person;

/**
 * Tests the Pub/Sub Json payload app.
 *
 * @author Farith
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { PubSubApplication.class })
public class PubSubJsonPayloadSampleApplicationTests {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	public void testReceivesJsonPayload() {
		Map<String, String> params = new MapBuilder<String, String>()
				.put("name", "Bob")
				.put("age", "25")
				.build();

		this.testRestTemplate.postForObject(
				"/createPerson?name={name}&age={age}", null, String.class, params);

		await().atMost(Duration.TEN_SECONDS).untilAsserted(() -> {
			ResponseEntity<List<Person>> response = this.testRestTemplate.exchange(
					"/listPeople",
					HttpMethod.GET,
					null,
					new ParameterizedTypeReference<List<Person>>() {
					});

			assertThat(response.getBody()).containsExactly(new Person("Bob", 25));
		});
	}
}
