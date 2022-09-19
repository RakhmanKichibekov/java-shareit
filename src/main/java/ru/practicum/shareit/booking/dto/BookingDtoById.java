package ru.practicum.shareit.booking.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoById {

    public BookingDtoById(Long id) {
        this.id = id;
    }
    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}