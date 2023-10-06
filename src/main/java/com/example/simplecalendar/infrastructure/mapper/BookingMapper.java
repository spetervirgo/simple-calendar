package com.example.simplecalendar.infrastructure.mapper;

import com.example.simplecalendar.domain.dto.BookingCreateDTO;
import com.example.simplecalendar.domain.model.Booking;
import org.mapstruct.Mapper;


/**
 * Interface for mapping {@link com.example.simplecalendar.domain.model.Booking} for different types.
 */
@Mapper(componentModel = "spring")
public interface BookingMapper {

	Booking toBooking(BookingCreateDTO bookingCreateDTO);
}
