package com.example.simplecalendar.infrastructure.resource;

import com.example.simplecalendar.domain.dto.BookingCreateDTO;
import com.example.simplecalendar.domain.model.Booking;
import com.example.simplecalendar.domain.service.BookingService;
import com.example.simplecalendar.infrastructure.mapper.BookingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/booking"})
public class BookingController {

	private final BookingService bookingService;
	private final BookingMapper bookingMapper;

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestBody BookingCreateDTO bookingCreateDTO) {
		try {
			Booking booking = bookingService.create(bookingMapper.toBooking(bookingCreateDTO));
			return ResponseEntity.status(HttpStatus.CREATED).body(booking);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/schedule")
	public ResponseEntity<List<Booking>> getWeeklySchedule(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		List<Booking> weeklySchedule = bookingService.getWeeklySchedule(date);
		return ResponseEntity.ok(weeklySchedule);
	}


	@GetMapping("/available-slots")
	public ResponseEntity<List<LocalDateTime>> getAvailableTimeSlots(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		List<LocalDateTime> availableTimeSlots = bookingService.getAvailableTimeSlots(date);
		return ResponseEntity.ok(availableTimeSlots);
	}

	@GetMapping
	public ResponseEntity<String> getBookingByDateTime(@RequestParam String dateTime) {
		try {
			LocalDateTime specifiedDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
			Optional<Booking> booking = bookingService.getByDateTime(specifiedDateTime);
			ResponseEntity<String> response;
			response = booking.map(value -> //
							ResponseEntity.ok("The meeting is booked by: " + value.getUserName())) //
					.orElseGet(() ->
							ResponseEntity.status(HttpStatus.NO_CONTENT).body("No booking found for the specified date: " + specifiedDateTime));
			return response;
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
