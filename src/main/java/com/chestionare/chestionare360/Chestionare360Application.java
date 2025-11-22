package com.chestionare.chestionare360;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class Chestionare360Application {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Bucharest"));
		SpringApplication.run(Chestionare360Application.class, args);
	}

}
