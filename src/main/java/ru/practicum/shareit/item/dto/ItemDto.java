package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
public class ItemDto {
    @Null
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    @Null
    private ItemDto.User owner;
    private Integer requestId;
    private ItemDto.Booking lastBooking;
    private ItemDto.Booking nextBooking;
    private List<ItemDto.Comment> comments;

    @Data
    @AllArgsConstructor
    public static class User {
        private Integer id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class Booking {
        private Integer id;
        private Integer bookerId;
    }

    @Data
    @AllArgsConstructor
    public static class Comment {
        private Integer id;
        private String text;
        private String authorName;
        private LocalDateTime created;
    }
}
