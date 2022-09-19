package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<List<Booking>> findByItem_Id(Long ItemId);
    List<Booking> findByBooker_IdOrderByStartDesc(Long id);
    Optional<List<Booking>> findByItem_IdAndBooker_id(Long aLong, Long aLong1);

}
