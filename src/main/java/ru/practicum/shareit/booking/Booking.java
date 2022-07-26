package ru.practicum.shareit.booking;

import lombok.*;

import java.time.LocalDateTime;

import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "start_date_time")
    private LocalDateTime start;
    @Column(name = "end_date_time")
    private LocalDateTime end;
    @ManyToOne()
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne()
    @JoinColumn(name = "booker_id")
    private User booker;
    @Column(name = "approved")
    private Boolean isApproved;
    @Column(name = "canceled")
    private Boolean isCanceled;

}