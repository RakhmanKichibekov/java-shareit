package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.exceptions.MessageFailedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Slf4j
@Service
public class BookingServiceImpl implements BookingService{

    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @PersistenceContext
    public EntityManager em;

    public BookingServiceImpl (BookingRepository bookingRepository, ItemRepository itemRepository,
                               UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }
    @Override
    public BookingDtoById createBooking (Optional<Long> idUser, Optional<BookingDtoIn> bookingDtoIn) throws ValidationException {
        User user = validationUser(idUser);
        Item item = validationItem(bookingDtoIn);
        if (user.getId().equals(item.getOwner().getId()))
            throw new NoSuchElementException("Бронирование запрошено собственником вещи. createBooking ()");
        if (!item.getAvailable())
            throw new ValidationException("Вещь недоступна для бронирования. createBooking ()");
        if (bookingDtoIn.get().getEnd().isBefore(bookingDtoIn.get().getStart()))
            throw new ValidationException  ("Дата окончания бронирования указана не верно! createBooking ()");
        LocalDateTime timeToday = LocalDateTime.now();
        if (bookingDtoIn.get().getEnd().isBefore(timeToday))
            throw new ValidationException  ("Дата окончания бронирования указана не верно! createBooking ()");
        if (bookingDtoIn.get().getStart().isBefore(timeToday))
            throw new ValidationException  ("Дата начала бронирования указана не верно! createBooking ()");
        Booking booking = BookingMapper.toBooking(bookingDtoIn.get());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        log.info("Забронирована вещь: {}", booking.getItem().getName());
        return BookingMapper.toBookingDtoById(booking);
    }

    public User validationUser (Optional<Long> idUser) throws ValidationException {
        if (!idUser.isPresent()) throw new NoSuchElementException("Отсутствует id владельца! validationUser()");
        Optional<User> user = userRepository.findById(idUser.get());
        if (!user.isPresent())
            throw new NoSuchElementException("Указанный пользователь не найден! validationUser()");
        return user.get();
    }
    public Item validationItem (Optional<BookingDtoIn> bookingDtoIn) throws ValidationException {
        Optional<Item> item = itemRepository.findById(bookingDtoIn.get().getItemId());
        if (!item.isPresent()) throw new NoSuchElementException("Такой вещи нет! validationItem()");
        return item.get();
    }
    public Booking validBooking (Optional<Long> bookingId) throws ValidationException {
        if (!bookingId.isPresent()) throw new ValidationException("Отсутствует id вещи! validationBooking()");
        Optional<Booking> booking = bookingRepository.findById(bookingId.get());
        if (!booking.isPresent())
            throw new NoSuchElementException("Указанный идентификатор брони не найден! validationBooking()");
        return booking.get() ;
    }
    @Override
    public BookingDtoOut patchStatusBooking (Optional<Long> idUser, Optional<Long> bookingId, Boolean approved)
            throws ValidationException {
        User user = validationUser(idUser);
        Booking booking = validBooking(bookingId);
        Optional<Item> item = itemRepository.findById(booking.getItem().getId());
        if (!item.isPresent())
            throw new NoSuchElementException("Вещь не найдена! patchStatusBooking()");
        if (!item.get().getOwner().getId().equals(user.getId()))
            throw new NoSuchElementException("Владелец вещи не совпадает! patchStatusBooking()");
        if (booking.getStatus().equals(Status.APPROVED) && approved)
            throw new ValidationException(String.format("Статус уже был ранее изменен на %S patchStatusBooking()", approved));
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else if (!approved) {
            booking.setStatus(Status.REJECTED);
        } else {
            throw new ValidationException("Не верный параметр approved вещи! patchStatusBooking()");
        }
        bookingRepository.save(booking);
        log.info("Изменен статус брони {} , на {}", booking.getId(), booking.getStatus());
        return BookingMapper.toBookingDto(booking);
    }
    @Override
    public List<BookingDtoOut> findBookingsAllById(Optional<Long> idUser) throws ValidationException {
        User user = validationUser(idUser);
        List<Booking> list =  bookingRepository.findByBooker_IdOrderByStartDesc(user.getId());
        log.info("Вся бронь пользователя {} ", user.getName());
        return BookingMapper.toListBookingDto(list);
    }
    @Override
    public BookingDtoOut findBookingById(Optional<Long> idUser, Optional<Long> bookingId) throws ValidationException {
        User user = validationUser(idUser);
        Booking booking = validBooking(bookingId);
        if (booking.getBooker().getId().equals(user.getId()) ||
                booking.getItem().getOwner().getId().equals(user.getId())) {
            log.info("Получена бронь номер {}", booking.getId());
            return BookingMapper.toBookingDto(booking);
        }
        throw new NoSuchElementException("Пользователю информация о бронировании не доступна, findBookingById");
    }
    @Override
    public List<BookingDtoOut> findBookingsState(Optional<Long> idUser, Optional<Integer> from, Optional<Integer> size, String state)
            throws ValidationException, MessageFailedException {
        try {
            State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new MessageFailedException();
        }
        User user = validationUser(idUser);
        switch (State.valueOf(state)) {
            case ALL:
                String request = "select b from Booking b where b.booker.id = ?1 order by b.start desc";
                LocalDateTime time = null;
                List <Booking> listBooking = requestBookingRepository (idUser, request, time, from, size);
                log.info("Вся бронь пользователя {}, со статусом ALL", user.getName());
                return BookingMapper.toListBookingDto(listBooking);
            case CURRENT:
                request = "select b from Booking b where b.booker.id = ?1 and ?2 between b.start and b.end order by b.start desc";
                time = LocalDateTime.now();
                listBooking = requestBookingRepository (idUser, request, time, from, size);
                log.info("Вся бронь пользователя {}, со статусом CURRENT", user.getName());
                return BookingMapper.toListBookingDto(listBooking);
            case PAST:
                request = "select b from Booking b where b.booker.id = ?1 and b.end < ?2 and b.status <> 'REJECTED' order by b.start desc";
                time = LocalDateTime.now();
                listBooking = requestBookingRepository (idUser, request, time, from, size);
                log.info("Вся бронь пользователя {}, со статусом PAST", user.getName());
                return BookingMapper.toListBookingDto(listBooking);
            case FUTURE:
                request = "select b from Booking b where b.booker.id = ?1 and b.start > ?2 order by b.start desc";
                time = LocalDateTime.now();
                listBooking = requestBookingRepository (idUser, request, time, from, size);
                log.info("Вся бронь пользователя {}, со статусом FUTURE", user.getName());
                return BookingMapper.toListBookingDto(listBooking);
            case WAITING:
                request = "select b from Booking b where b.booker.id = ?1 and b.status = 'WAITING' order by b.start desc";
                time = null;
                listBooking = requestBookingRepository (idUser, request, time, from, size);
                log.info("Вся бронь пользователя {}, со статусом WAITING", user.getName());
                return BookingMapper.toListBookingDto(listBooking);
            case REJECTED:
                request = "select b from Booking b where b.booker.id = ?1 and b.status = 'REJECTED' order by b.start desc";
                time = null;
                listBooking = requestBookingRepository (idUser, request, time, from, size);
                log.info("Вся бронь пользователя {}, со статусом REJECTED", user.getName());
                return BookingMapper.toListBookingDto(listBooking);
            default:
                throw new MessageFailedException();
        }
    }
    public List<Booking> requestBookingRepository (Optional<Long> idUser, String request, LocalDateTime time,
                                                   Optional<Integer> from, Optional<Integer> size) throws ValidationException {
        List<Booking> list;
        if (!from.isPresent() || !size.isPresent()) {
            if (time == null) {
                list = em.createQuery(request, Booking.class)
                        .setParameter(1, idUser.get())
                        .getResultList();
            } else {
                list = em.createQuery(request, Booking.class)
                        .setParameter(1, idUser.get())
                        .setParameter (2, time)
                        .getResultList();
            }
        } else if (from.get() < 0 || size.get() <= 0) {
            throw new ValidationException("Параметры from, size заданы не верно! requestBookingRepository()");
        } else if (time == null) {
            list = em.createQuery(request, Booking.class)
                    .setParameter (1, idUser.get())
                    .setFirstResult(from.get())
                    .setMaxResults(size.get())
                    .getResultList();
        } else {
            list = em.createQuery(request, Booking.class)
                    .setParameter (1, idUser.get())
                    .setParameter (2, time)
                    .setFirstResult(from.get())
                    .setMaxResults(size.get())
                    .getResultList();
        }
        return list;
    }
    @Override
    public List<BookingDtoOut> findBookingsOwnerState (Optional<Long> idUser, Optional<Integer> from,
                                                       Optional<Integer> size, String state) throws ValidationException, MessageFailedException {
        try {
            State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new MessageFailedException();
        }
        User user = validationUser(idUser);
        if (itemRepository.findByOwner_IdOrderById(idUser.get()).isEmpty())
            throw new ValidationException("У пользователя нет вещей! findBookingsOwnerState()");
        switch (State.valueOf(state)) {
            case ALL:
                String request = "select b from Booking b where b.item.owner.id = ?1 order by b.start desc";
                LocalDateTime time = null;
                List <Booking> listBooking = requestBookingRepository (idUser, request, time, from, size);
                log.info("Вся бронь собственника {}, со статусом ALL", user.getName());
                return BookingMapper.toListBookingDto(listBooking);
            case CURRENT:
                request = "select b from Booking b where b.item.owner.id = ?1 and ?2 between b.start and b.end order by b.start desc";
                time = LocalDateTime.now();
                listBooking = requestBookingRepository (idUser, request, time, from, size);
                log.info("Вся бронь собственника {}, со статусом CURRENT", user.getName());
                return BookingMapper.toListBookingDto(listBooking);
            case PAST:
                request = "select b from Booking b where b.item.owner.id = ?1 and b.end < ?2 and b.status <> 'REJECTED' order by b.start desc";
                time = LocalDateTime.now();
                listBooking = requestBookingRepository (idUser, request, time, from, size);
                log.info("Вся бронь собственника {}, со статусом PAST", user.getName());
                return BookingMapper.toListBookingDto(listBooking);
            case FUTURE:
                request = "select b from Booking b where b.item.owner.id = ?1 and b.start > ?2 order by b.start desc";
                time = LocalDateTime.now();
                listBooking = requestBookingRepository (idUser, request, time, from, size);
                log.info("Вся бронь собственника {}, со статусом FUTURE", user.getName());
                return BookingMapper.toListBookingDto(listBooking);
            case WAITING:
                request = "select b from Booking b where b.item.owner.id = ?1 and b.status = 'WAITING' order by b.start desc";
                time = null;
                listBooking = requestBookingRepository (idUser, request, time, from, size);
                log.info("Вся бронь собственника {}, со статусом WAITING", user.getName());
                return BookingMapper.toListBookingDto(listBooking);
            case REJECTED:
                request = "select b from Booking b where b.item.owner.id = ?1 and b.status = 'REJECTED' order by b.start desc";
                time = null;
                listBooking = requestBookingRepository (idUser, request, time, from, size);
                log.info("Вся бронь собственника {}, со статусом REJECTED", user.getName());
                return BookingMapper.toListBookingDto(listBooking);
            default:
                throw new MessageFailedException();
        }
    }

}