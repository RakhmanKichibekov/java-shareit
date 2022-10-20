package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class MapperTest {
    private final BookingMapper bookingMapper = new BookingMapper();
    private final BookingDto bookingDto1 = new BookingDto();
    private final Booking booking1 = new Booking();
    private final User user1 = new User();
    private final Item item = new Item();

    @BeforeEach
    public void init() {
        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        item.setId(1);
        item.setName("item1");
        item.setDescription("descr item1");
        item.setAvailable(true);
        item.setOwner(user1.getId());
        bookingDto1.setId(1);
        bookingDto1.setItemId(item.getId());
        bookingDto1.setUserId(user1.getId());
        bookingDto1.setStart(LocalDateTime.now().plusHours(1).withNano(0));
        bookingDto1.setEnd(LocalDateTime.now().plusHours(2).withNano(0));
        bookingDto1.setStatus(Status.WAITING);
        bookingDto1.setBooker(user1);
        bookingDto1.setItem(item);

        booking1.setId(1);
        booking1.setStart(LocalDateTime.now().plusHours(1).withNano(0));
        booking1.setEnd(LocalDateTime.now().plusHours(2).withNano(0));
        booking1.setBookerId(user1.getId());
        booking1.setItem(item.getId());
        booking1.setStatus(Status.WAITING);
    }

    @Test
    public void fromBookingToDtoTest() {
        BookingDto bookingDtoResult = bookingMapper.toDto(booking1, item, user1);
        Assertions.assertEquals(bookingDtoResult, bookingDto1);
    }

    @Test
    public void fromDtoToBookingTest() {
        Booking bookingResult = bookingMapper.toBooking(bookingDto1, user1.getId());
        Assertions.assertEquals(bookingResult, booking1);
    }

    @Test
    public void fromStatusDtoToStatusTest() {
        Status status = Status.WAITING;
        Status statusResult = bookingMapper.toStatus(StatusDto.WAITING);
        Assertions.assertEquals(statusResult, status);
    }
}
