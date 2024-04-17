package com.abach42.superhero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SuperheroApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuperheroApplication.class, args);
	}
}
