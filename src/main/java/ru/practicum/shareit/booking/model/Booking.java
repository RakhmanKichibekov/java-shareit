package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.booking.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "start_date")
    private LocalDateTime start;

    @Column(name = "end_date")
    private LocalDateTime end;

    @Column(name = "item_id")
    private Integer item;

    @Column(name = "user_id")
    private Integer bookerId;

    @Enumerated(EnumType.STRING)
    private Status status;
}