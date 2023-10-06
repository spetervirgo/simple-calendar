package com.example.simplecalendar.service;

import com.example.simplecalendar.domain.model.Booking;
import com.example.simplecalendar.domain.repository.BookingRepository;
import com.example.simplecalendar.domain.service.BookingService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@ComponentScan(basePackages = "com.example")
@DataJpaTest
class BookingServiceTest {

	@Autowired
	private BookingService bookingService;

	@Autowired
	private BookingRepository bookingRepository;

	@Test
	void testCreateBooking_longerThanAllowed() {
		Booking booking = new Booking();
		booking.setUserName("Test");
		booking.setStartTime(LocalDateTime.of(2023, 10, 6, 9, 0));
		booking.setEndTime(LocalDateTime.of(2023, 10, 6, 12, 30));


		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			bookingService.create(booking);
		});

		assertEquals("Booking durations is not multiple of [30] minutes or more than [3] hours", exception.getMessage());
	}

	@Test
	void testCreateBooking_notMultipleOf30() {
		Booking booking = new Booking();
		booking.setUserName("Test");
		booking.setStartTime(LocalDateTime.of(2023, 10, 6, 9, 0));
		booking.setEndTime(LocalDateTime.of(2023, 10, 6, 10, 45));


		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			bookingService.create(booking);
		});

		assertEquals("Booking does not start or ends at whole or half hours.", exception.getMessage());
	}

	@Test
	void testCreateBooking_outsideOfTheInterval() {
		Booking booking = new Booking();
		booking.setUserName("Test");
		booking.setStartTime(LocalDateTime.of(2023, 10, 6, 8, 0));
		booking.setEndTime(LocalDateTime.of(2023, 10, 6, 9, 0));


		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			bookingService.create(booking);
		});

		assertEquals("Booking is not in the allowed range time.", exception.getMessage());
	}

	@Test
	void testCreateBooking_weekend() {
		Booking booking = new Booking();
		booking.setUserName("Test");
		booking.setStartTime(LocalDateTime.of(2023, 10, 7, 9, 0));
		booking.setEndTime(LocalDateTime.of(2023, 10, 7, 10, 0));


		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			bookingService.create(booking);
		});

		assertEquals("Booking is available on weekdays.", exception.getMessage());
	}

	@Test
	@Transactional
	void testCreateBookingWithOverlap() {
		Booking existingBooking = new Booking();
		existingBooking.setUserName("Booking1");
		existingBooking.setStartTime(LocalDateTime.of(2023, 10, 6, 9, 0));
		existingBooking.setEndTime(LocalDateTime.of(2023, 10, 6, 10, 0));
		bookingService.create(existingBooking);

		Booking booking = new Booking();
		booking.setUserName("Booking2");
		booking.setStartTime(LocalDateTime.of(2023, 10, 6, 9, 30));
		booking.setEndTime(LocalDateTime.of(2023, 10, 6, 10, 0));

		Throwable exception = assertThrows(IllegalStateException.class, () -> bookingService.create(booking));

		assertEquals("There is a booking already at the given time.", exception.getMessage());
	}

	@Test
	void testCreateBooking_notStartAtWholeOrHalfHour() {
		Booking booking = new Booking();
		booking.setUserName("Test");
		booking.setStartTime(LocalDateTime.of(2023, 10, 6, 10, 10));
		booking.setEndTime(LocalDateTime.of(2023, 10, 6, 11, 10));


		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			bookingService.create(booking);
		});

		assertEquals("Booking does not start or ends at whole or half hours.", exception.getMessage());
	}

	@Test
	void testCreateBooking_fourBookingSameDay() {
		LocalDateTime startTime = LocalDateTime.of(2023, 10, 5, 9, 0);
		for (int i = 0; i < 4; i++) {
			bookingService.create(createBooking(startTime, startTime.plusHours(2), "People" + i));
			startTime = startTime.plusHours(2);
		}
		List<Booking> bookings = bookingService.getWeeklySchedule(startTime.toLocalDate());
		Assertions.assertEquals(4, bookings.size());
	}

	private Booking createBooking(LocalDateTime startTime, LocalDateTime endTime, String name) {
		Booking booking = new Booking();
		booking.setUserName(name);
		booking.setStartTime(startTime);
		booking.setEndTime(endTime);
		return booking;
	}
}
