package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
public class BookingDto {
    private LocalDate start;
    private LocalDate end;
    private String item;
    private String booker;
}
