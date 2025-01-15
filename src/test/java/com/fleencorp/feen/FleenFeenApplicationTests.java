package com.fleencorp.feen;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FleenFeenApplicationTests {

	@Test
	void contextLoads() {
		final String testText = "Test Feen App";
		assertNotNull(testText);
	}

}
