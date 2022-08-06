package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingValidateException;
import ru.practicum.shareit.booking.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.exception.AccessIsDeniedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingDto add(Integer userId, BookingDto bookingDto) {
        log.info("Добавление бронирования вещи с id {} пользователем c id {}.", bookingDto.getItemId(), userId);
        User user = UserMapper.toUser(userService.findById(userId));
        Item item = itemService.findItemOrException(bookingDto.getItemId());
        if (user.getId().equals(item.getOwner().getId())) {
            throw new AccessIsDeniedException("Владелец вещи не может быть ее арендатором.");
        }
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Вещь с id " + item + " не доступна для бронирования.");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new BookingValidateException("Неверно указан период бронирования.");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())
                || bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BookingValidateException("Неверно указан период бронирования.");
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approveBooking(Integer userId, Integer bookingId, Boolean approved) {
        log.info("Изменение статуса бронирования с id {} пользователем c id {}.", bookingId, userId);
        if (approved == null) {
            throw new BookingValidateException("Не указан статус.");
        }
        Booking booking = findBookingOrException(bookingId);
        User user = userService.findUserOrException(userId);
        Item item = itemService.findItemOrException(booking.getItem().getId());
        if (!item.getOwner().getId().equals(user.getId())) {
            throw new AccessIsDeniedException("Недостаточно прав для выполнения операции.");
        }
        if (booking.getIsApproved() && !booking.getIsCanceled() && approved) {
            throw new BookingValidateException("Статус 'Подтвержден' уже установлен.");
        }
        if (approved) {
            booking.setIsApproved(true);
            booking.setIsCanceled(false);
        } else {
            booking.setIsApproved(true);
            booking.setIsCanceled(true);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Integer userId, Integer bookingId) {
        log.info("Найти бронирование c id {} пользователем с id {}.", bookingId, userId);
        Booking booking = findBookingOrException(bookingId);
        User user = userService.findUserOrException(userId);
        if (!booking.getBooker().getId().equals(user.getId())) {
            if (!booking.getItem().getOwner().getId().equals(user.getId())) {
                throw new AccessIsDeniedException("Недостаточно прав для выполнения операции.");
            }
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findByBooker(Integer userId, State state) {
        log.info("Получить список бронирований пользователя с id {}.", userId);
        userService.findUserOrException(userId);
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerId(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findCurrentByBooker(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findPastByBooker(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findFutureByBooker(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findWaitingOrRejectedByBooker(userId,
                                false, false)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findWaitingOrRejectedByBooker(userId,
                                true, true)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return List.of();
        }
    }

    @Override
    public List<BookingDto> findByOwner(Integer userId, State state) {
        userService.findUserOrException(userId);
        log.info("Получить список бронирований пользователя-владельца с id {}", userId);
        switch (state) {
            case ALL:
                return bookingRepository.findAllByOwnerId(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findPastByOwner(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findCurrentByOwner(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findFutureByOwner(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findWaitingOrRejectedByOwner(userId,
                                false, false)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findWaitingOrRejectedByOwner(userId,
                                true, true)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return List.of();
        }
    }

    @Override
    public Booking findBookingOrException(Integer id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.orElseThrow(() -> new BookingNotFoundException("Бронирование с id " + id + " не найдено."));
    }
}