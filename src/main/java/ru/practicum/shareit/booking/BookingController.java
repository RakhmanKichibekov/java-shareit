package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @RequestBody BookingDto bookingDto) {
        log.debug("Добавить бронирование вещи с id {} пользователем c id {}.", bookingDto.getItemId(), userId);
        return service.add(userId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    @ResponseStatus(OK)
    public BookingDto approveBooking(@PathVariable Integer bookingId,
                                     @RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @RequestParam Boolean approved) {
        log.debug("Изменение статуса бронирования с id {} пользователем c id {}.", bookingId, userId);
        return service.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(OK)
    public BookingDto findById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                               @PathVariable Integer bookingId) {
        log.debug("Найти бронирование c id {} пользователем с id {}.", bookingId, userId);
        return service.findById(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<BookingDto> findByUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                       @RequestParam(defaultValue = "ALL") State state) {
        log.debug("Получить список бронирований пользователя с id {}.", userId);
        return service.findByBooker(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(OK)
    public List<BookingDto> findBookingByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @RequestParam(defaultValue = "ALL") State state) {
        log.debug("Получить список бронирований пользователя-владельца с id {}.", userId);
        return service.findByOwner(userId, state);
    }
}
