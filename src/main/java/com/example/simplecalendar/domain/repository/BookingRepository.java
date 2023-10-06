package com.example.simplecalendar.domain.repository;

import com.example.simplecalendar.domain.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	@Query("SELECT b FROM Booking b WHERE b.startTime <= :dateTime AND b.endTime > :dateTime")
	Optional<Booking> findByStartTimeLessThanEqualAndEndTimeGreaterThan(LocalDateTime dateTime);

	List<Booking> findAllByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

	List<Booking> findByStartTimeLessThanAndEndTimeGreaterThan(LocalDateTime endTime, LocalDateTime startTime);
}
