package gcp;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class PubSubApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(PubSubApplication.class, args);
	}

}
