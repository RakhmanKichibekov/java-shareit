package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {

    @Null
    private Integer id;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @FutureOrPresent
    private LocalDateTime end;
    @Null
    private BookingDto.Item item;
    @Null
    private BookingDto.User booker;
    private Integer itemId;
    @Null
    private Status status;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private Integer id;
        private String name;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Item {
        private Integer id;
        private String name;
    }
}
