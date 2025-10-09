package com.jose.sicov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SicovApplication {

	public static void main(String[] args) {
		SpringApplication.run(SicovApplication.class, args);
	}

}
