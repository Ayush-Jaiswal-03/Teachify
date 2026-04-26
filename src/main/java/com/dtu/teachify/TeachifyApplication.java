package com.dtu.teachify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TeachifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeachifyApplication.class, args);
	}

}
