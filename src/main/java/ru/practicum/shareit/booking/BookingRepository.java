package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query(value = "" +
            "SELECT * " +
            "FROM bookings AS b " +
            "WHERE b.booker_id = ?1 " +
            "AND NOT b.canceled " +
            "ORDER BY b.start_date_time DESC",
            nativeQuery = true)
    List<Booking> findAllByBookerId(Integer bookerId);

    @Query(value = "" +
            "SELECT * " +
            "FROM bookings " +
            "WHERE booker_id = ?1 " +
            "AND start_date_time <= ?2 " +
            "AND end_date_time >= ?2",
            nativeQuery = true)
    List<Booking> findCurrentByBooker(Integer bookerId, LocalDateTime localDateTime);

    @Query(value = "" +
            "SELECT * " +
            "FROM bookings " +
            "WHERE booker_id = ?1 " +
            "AND end_date_time < ?2",
            nativeQuery = true)
    List<Booking> findPastByBooker(Integer bookerId, LocalDateTime localDateTime);

    @Query(value = "" +
            "SELECT * " +
            "FROM bookings " +
            "WHERE booker_id = ?1 " +
            "AND start_date_time > ?2",
            nativeQuery = true)
    List<Booking> findFutureByBooker(Integer bookerId, LocalDateTime localDateTime);

    @Query(value = "SELECT * " +
            "FROM bookings " +
            "WHERE booker_id = ?1 " +
            "AND (approved = ?2) " +
            "AND (canceled = ?3) ",
            nativeQuery = true)
    List<Booking> findWaitingOrRejectedByBooker(Integer bookerId, Boolean approved, Boolean canceled);

    @Query(value = "" +
            "SELECT b.* " +
            "FROM bookings AS b " +
            "INNER JOIN items AS i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "ORDER BY start_date_time DESC ",
            nativeQuery = true)
    List<Booking> findAllByOwnerId(Integer ownerId);

    @Query(value = "" +
            "SELECT b.* " +
            "FROM bookings AS b" +
            "INNER JOIN items AS i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND end_date_time < ?2",
            nativeQuery = true)
    List<Booking> findPastByOwner(Integer ownerId, LocalDateTime localDateTime);

    @Query(value = "" +
            "SELECT b.* " +
            "FROM bookings AS b" +
            "INNER JOIN items AS i ON i.id = b.item_id " +
            "WHERE i.owner_id = :?1 " +
            "AND start_date_time <= ?2 " +
            "AND end_date_time >= ?2",
            nativeQuery = true)
    List<Booking> findCurrentByOwner(Integer ownerId, LocalDateTime localDateTime);

    @Query(value = "" +
            "SELECT b.* " +
            "FROM bookings AS b" +
            "INNER JOIN items AS i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND start_date_time > ?2",
            nativeQuery = true)
    List<Booking> findFutureByOwner(Integer ownerId, LocalDateTime localDateTime);

    @Query(value = "" +
            "SELECT b.* " +
            "FROM bookings AS b" +
            "INNER JOIN items AS i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND (approved = ?2) " +
            "AND (canceled = ?3)",
            nativeQuery = true)
    List<Booking> findWaitingOrRejectedByOwner(Integer ownerId, Boolean approved, Boolean canceled);

    @Query(value = "" +
            "SELECT * " +
            "FROM bookings " +
            "WHERE item_id = ?1 " +
            "AND end_date_time < ?2 " +
            "ORDER BY end_date_time " +
            "DESC LIMIT 1",
            nativeQuery = true)
    Booking getLastBooking(Integer itemId, LocalDateTime localDateTime);

    @Query(value = "" +
            "SELECT * " +
            "FROM bookings " +
            "WHERE item_id = ?1 " +
            "AND start_date_time > ?2 " +
            "ORDER BY start_date_time " +
            "LIMIT 1",
            nativeQuery = true)
    Booking getNextBooking(Integer itemId, LocalDateTime localDateTime);


}
