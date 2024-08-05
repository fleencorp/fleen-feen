package com.fleencorp.feen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class FleenFeenApplication {

	public static void main(final String[] args) {
		SpringApplication.run(FleenFeenApplication.class, args);
	}
}
