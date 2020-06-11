package org.rodriguez.noelsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class NoelSpPapApplication {

	public static void main(String[] args) {
		SpringApplication.run(NoelSpPapApplication.class, args);
	}

}
