package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ServiceTest {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    private final BookingDto bookingDto1 = new BookingDto();
    private final BookingDto bookingDto2 = new BookingDto();
    private final Booking booking1 = new Booking();
    private final Booking booking2 = new Booking();
    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item = new Item();

    @BeforeEach
    public void init() {
        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        user2.setId(2);
        user2.setName("user2");
        user2.setEmail("u2@user.com");
        item.setId(1);
        item.setName("item1");
        item.setDescription("descr item1");
        item.setAvailable(true);
        item.setOwner(user1.getId());

        bookingDto1.setId(1);
        bookingDto1.setItemId(item.getId());
        bookingDto1.setUserId(user2.getId());
        bookingDto1.setStart(LocalDateTime.now().plusHours(1).withNano(0));
        bookingDto1.setEnd(LocalDateTime.now().plusHours(2).withNano(0));
        bookingDto1.setStatus(Status.WAITING);
        bookingDto1.setBooker(user2);
        bookingDto1.setItem(item);

        bookingDto2.setId(2);
        bookingDto2.setItemId(item.getId());
        bookingDto2.setUserId(user2.getId());
        bookingDto2.setStart(LocalDateTime.now().plusHours(3).withNano(0));
        bookingDto2.setEnd(LocalDateTime.now().plusHours(4).withNano(0));
        bookingDto2.setStatus(Status.WAITING);
        bookingDto2.setBooker(user2);
        bookingDto2.setItem(item);

        booking1.setId(1);
        booking1.setStart(LocalDateTime.now().plusHours(1).withNano(0));
        booking1.setEnd(LocalDateTime.now().plusHours(2).withNano(0));
        booking1.setBookerId(2);
        booking1.setItem(1);
        booking1.setStatus(Status.WAITING);

        booking2.setId(2);
        booking2.setStart(LocalDateTime.now().plusHours(3).withNano(0));
        booking2.setEnd(LocalDateTime.now().plusHours(4).withNano(0));
        booking2.setBookerId(2);
        booking2.setItem(1);
        booking2.setStatus(Status.WAITING);
    }

    @Test
    public void addBookingTest() {
        userService.add(user1);
        userService.add(user2);
        itemService.add(item);
        BookingDto bookingDtoResult = bookingService.add(bookingDto1, user2.getId());
        Assertions.assertEquals(bookingDtoResult, bookingDto1);
    }

    @Test
    public void addBookingWithInvalidUserTest() {
        Assertions.assertThrows(ResponseStatusException.class, () -> bookingService.add(bookingDto1, 99));
    }

    @Test
    public void addBookingWithInvalidItemTest() {
        bookingDto1.setItemId(99);
        Assertions.assertThrows(ResponseStatusException.class, () -> bookingService.add(bookingDto1, user2.getId()));
    }

    @Test
    public void addBookingWhenBookerIsOwnerOfItemTest() {
        bookingDto1.setUserId(user1.getId());
        Assertions.assertThrows(ResponseStatusException.class, () -> bookingService.add(bookingDto1, user1.getId()));
    }

    @Test
    public void updateApproveBookingTest() {
        userService.add(user1);
        userService.add(user2);
        itemService.add(item);
        bookingService.add(bookingDto1, user2.getId());
        BookingDto bookingDtoResult = bookingService.updApprove(booking1.getId(), true, user1.getId());
        Assertions.assertEquals(bookingDtoResult.getId(), bookingDto1.getId());
        Assertions.assertEquals(bookingDtoResult.getItem(), bookingDto1.getItem());
        Assertions.assertEquals(bookingDtoResult.getBooker(), bookingDto1.getBooker());
        Assertions.assertEquals(bookingDtoResult.getStatus(), Status.APPROVED);
    }

    @Test
    public void updateApproveBookingWhenUpdaterIsNotOwnerOfItemTest() {
        userService.add(user1);
        userService.add(user2);
        itemService.add(item);
        bookingService.add(bookingDto1, user2.getId());
        Assertions.assertThrows(ResponseStatusException.class,
                () -> bookingService.updApprove(bookingDto1.getId(), true, user2.getId()));
    }

    @Test
    public void findBookingByIdTest() {
        userService.add(user1);
        userService.add(user2);
        itemService.add(item);
        bookingService.add(bookingDto1, user2.getId());
        BookingDto bookingDtoResult = bookingService.findById(1, user1.getId());
        Assertions.assertEquals(bookingDtoResult, bookingDto1);
    }

    @Test
    public void findAllBookingByUser() {
        userService.add(user1);
        userService.add(user2);
        itemService.add(item);
        bookingService.add(bookingDto1, user2.getId());
        bookingService.add(bookingDto2, user2.getId());
        Collection<BookingDto> result = bookingService.findAllByUser(user2.getId(), StatusDto.ALL, 0, 10);
        Assertions.assertEquals(result, List.of(bookingDto2, bookingDto1));
        result = bookingService.findAllByUser(user2.getId(), StatusDto.WAITING, 0, 10);
        Assertions.assertEquals(result, List.of(bookingDto2, bookingDto1));
        result = bookingService.findAllByUser(user2.getId(), StatusDto.FUTURE, 0, 10);
        Assertions.assertEquals(result, List.of(bookingDto2, bookingDto1));
        result = bookingService.findAllByUser(user2.getId(), StatusDto.PAST, 0, 10);
        Assertions.assertEquals(result, List.of());
        result = bookingService.findAllByUser(user2.getId(), StatusDto.CURRENT, 0, 10);
        Assertions.assertEquals(result, List.of());
    }

    @Test
    public void findAllBookingByOwner() {
        userService.add(user1);
        userService.add(user2);
        itemService.add(item);
        bookingService.add(bookingDto1, user2.getId());
        bookingService.add(bookingDto2, user2.getId());
        Collection<BookingDto> result = bookingService.findAllByOwner(user1.getId(), StatusDto.ALL, 0, 10);
        Assertions.assertEquals(result, List.of(bookingDto2, bookingDto1));
        result = bookingService.findAllByOwner(user1.getId(), StatusDto.WAITING, 0, 10);
        Assertions.assertEquals(result, List.of(bookingDto2, bookingDto1));
        result = bookingService.findAllByOwner(user1.getId(), StatusDto.FUTURE, 0, 10);
        Assertions.assertEquals(result, List.of(bookingDto2, bookingDto1));
        result = bookingService.findAllByOwner(user1.getId(), StatusDto.PAST, 0, 10);
        Assertions.assertEquals(result, List.of());
        result = bookingService.findAllByOwner(user1.getId(), StatusDto.CURRENT, 0, 10);
        Assertions.assertEquals(result, List.of());
    }
}
