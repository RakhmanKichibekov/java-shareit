package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto add(Integer userId, BookingDto bookingDto);

    BookingDto findById(Integer userId, Integer bookingId);

    BookingDto approveBooking(Integer userId, Integer bookingId, Boolean approved);

    List<BookingDto> findByBooker(Integer userId, State state);

    List<BookingDto> findByOwner(Integer userId, State state);

    Booking findBookingOrException(Integer id);
} 