
package com.v01.techgear_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@EnableCaching
@SpringBootApplication
public class TechgearServerApplication {

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) throws JsonMappingException, JsonProcessingException {

		SpringApplication.run(TechgearServerApplication.class, args);
	}

}
