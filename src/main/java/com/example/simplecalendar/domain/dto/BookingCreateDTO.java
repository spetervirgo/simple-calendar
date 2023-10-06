package com.example.simplecalendar.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
public class BookingCreateDTO {
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime startTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime endTime;
	private String userName;
}
