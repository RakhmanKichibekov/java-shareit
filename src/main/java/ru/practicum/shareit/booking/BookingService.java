package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoById;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.exceptions.MessageFailedException;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    public BookingDtoById createBooking(Optional<Long> idUser, Optional<BookingDtoIn> bookingDtoIn)
            throws ValidationException;

    BookingDtoOut patchStatusBooking(Optional<Long> idUser, Optional<Long> approved, Boolean bookingId) throws ValidationException;

    BookingDtoOut findBookingById(Optional<Long> idUser, Optional<Long> bookingId) throws ValidationException;

    List<BookingDtoOut> findBookingsState(Optional<Long> idUser, Optional<Integer> from, Optional<Integer> size, String state)
            throws ValidationException, MessageFailedException;

    List<BookingDtoOut> findBookingsOwnerState(Optional<Long> idUser, Optional<Integer> from, Optional<Integer> size, String state)
            throws ValidationException, MessageFailedException;

    List<BookingDtoOut> findBookingsAllById(Optional<Long> userId) throws ValidationException;
}