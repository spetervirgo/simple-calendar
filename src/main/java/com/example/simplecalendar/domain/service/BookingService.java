package com.example.simplecalendar.domain.service;

import com.example.simplecalendar.domain.model.Booking;
import com.example.simplecalendar.domain.repository.BookingRepository;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BookingService {

	private static final int WEEK_DAY_START_HOUR = 9;
	private static final int WEEK_DAY_END_HOUR = 17;
	private static final int INTERVAL_MINUTES = 30;
	private static final int DURATION_MIN_MINUTES = 30;
	private static final int DURATION_MAX_HOURS = 3;

	private final BookingRepository bookingRepository;

	public Booking create(Booking booking) {
		isValidBookingBooking(booking);
		return bookingRepository.save(booking);
	}

	private void isValidBookingBooking(Booking bookingToCreate) {
		LocalDateTime startTime = bookingToCreate.getStartTime();
		LocalDateTime endTime = bookingToCreate.getEndTime();

		// Check required fields
		if (bookingToCreate.getUserName() == null || bookingToCreate.getStartTime() == null || bookingToCreate.getEndTime() == null) {
			throw new IllegalStateException("Fields are required.");
		}

		// Check if the booking is on a valid weekday (Monday to Friday)
		if (DayOfWeek.SATURDAY.equals(startTime.getDayOfWeek()) || DayOfWeek.SUNDAY.equals(startTime.getDayOfWeek())) {
			throw new IllegalStateException("Booking is available on weekdays.");
		}

		// Check if booking end time greater than start time
		if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
			throw new IllegalStateException("End time of the booking should be greater than the start time.");
		}

		// Check if booking starts and ends within the allowed time range (09:00 - 17:00)
		if (startTime.getHour() < WEEK_DAY_START_HOUR || startTime.getHour() > WEEK_DAY_END_HOUR || endTime.getHour() > WEEK_DAY_END_HOUR) {
			throw new IllegalStateException("Booking is not in the allowed range time.");
		}

		// Check if booking starts and ends whole hours or half hours
		if (startTime.getMinute() % INTERVAL_MINUTES != 0 || endTime.getMinute() % INTERVAL_MINUTES != 0) {
			throw new IllegalStateException("Booking does not start or ends at whole or half hours.");
		}

		// Check if the booking duration is a multiple of 30 minutes and does not exceed 3 hours
		Duration duration = Duration.between(startTime, endTime);
		if (duration.toMinutes() < 30 || duration.toMinutes() > 60 * DURATION_MAX_HOURS) {
			throw new IllegalStateException(MessageFormat.format("Booking durations is not multiple of [{0}] minutes or more than [{1}] hours", DURATION_MIN_MINUTES, DURATION_MAX_HOURS));
		}

		// Check if the booking does not overlap with existing bookings
		List<Booking> existingBookings = bookingRepository.findByStartTimeLessThanAndEndTimeGreaterThan(endTime, startTime);
		if (!existingBookings.isEmpty()) {
			throw new IllegalStateException("There is a booking already at the given time.");
		}
	}

	public Optional<Booking> getByDateTime(LocalDateTime dateTime) {
		return bookingRepository.findByStartTimeLessThanEqualAndEndTimeGreaterThan(dateTime);
	}

	public List<Booking> getWeeklySchedule(LocalDate date) {
		LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate endOfWeek = startOfWeek.plusDays(6);

		return bookingRepository.findAllByStartTimeBetween(
				startOfWeek.atStartOfDay(),
				endOfWeek.atTime(LocalTime.MAX)
		);
	}

	public List<LocalDateTime> getAvailableTimeSlots(LocalDate date) {
		LocalDateTime startTime = date.atTime(WEEK_DAY_START_HOUR, 0);
		LocalDateTime endTime = date.atTime(WEEK_DAY_END_HOUR, 0);

		List<LocalDateTime> bookedTimeSlots = bookingRepository
				.findAllByStartTimeBetween(startTime, endTime)
				.stream()
				.flatMap(booking -> generateTimeSlots(booking.getStartTime(), booking.getEndTime()).stream())
				.toList();

		List<LocalDateTime> availableTimeSlots = new ArrayList<>();
		LocalDateTime currentSlot = startTime;

		while (currentSlot.isBefore(endTime)) {
			boolean isBooked = bookedTimeSlots.contains(currentSlot);

			if (!isBooked || !bookedTimeSlots.contains(currentSlot.plusMinutes(30))) {
				availableTimeSlots.add(currentSlot);
			}
			currentSlot = currentSlot.plusMinutes(INTERVAL_MINUTES);
		}

		return availableTimeSlots;
	}

	private List<LocalDateTime> generateTimeSlots(LocalDateTime start, LocalDateTime end) {
		List<LocalDateTime> timeSlots = new ArrayList<>();
		LocalDateTime currentSlot = start;

		while (currentSlot.isBefore(end) || currentSlot.equals(end)) {
			timeSlots.add(currentSlot);
			currentSlot = currentSlot.plusMinutes(INTERVAL_MINUTES);
		}

		return timeSlots;
	}
}
