package com.example.simplecalendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.example.simplecalendar"})
public class SimpleCalendarApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleCalendarApplication.class, args);
	}
}
