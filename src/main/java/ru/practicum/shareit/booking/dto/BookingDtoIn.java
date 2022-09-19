package ru.practicum.shareit.booking.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoIn {

    @NotBlank
    private Long itemId;
    @NotBlank
    private LocalDateTime start;
    @NotBlank
    private LocalDateTime end;

}