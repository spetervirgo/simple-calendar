package com.example.simplecalendar.infrastructure.configuration;

import com.example.simplecalendar.domain.repository.BookingRepository;
import com.example.simplecalendar.domain.service.BookingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Configuration for creating service beans
 */
@Configuration
public class ServiceBeanConfiguration {

	@Bean
	public BookingService bookingService(BookingRepository bookingRepository) {
		return new BookingService(bookingRepository);
	}
}
