package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static BookingDtoOut toBookingDto(Booking booking) {
        return new BookingDtoOut(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                booking.getBooker(),
                booking.getItem()

        );
    }

    public static BookingDtoById toBookingDtoById(Booking booking) {
        return new BookingDtoById (
                booking.getId()
        );
    }
    public static BookingDtoById toBookingDtoByIdTime(Booking booking) {
        return new BookingDtoById (
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }

    public static Booking toBooking(BookingDtoIn bookingDtoIn) {
        return new Booking (
                bookingDtoIn.getStart(),
                bookingDtoIn.getEnd()
        );
    }

    public static List<BookingDtoOut> toListBookingDto (List<Booking> listBooking) {
        List<BookingDtoOut> listDto = new ArrayList<>();
        for (Booking booking : listBooking) {
            listDto.add(toBookingDto(booking));
        }
        return listDto;
    }

}
