package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ItemRequestDto {

    @Null
    private Integer id;
    @NotBlank
    private String description;
    @Null
    private ItemRequestDto.User requester;
    @Null
    private LocalDate created;

    @Data
    @AllArgsConstructor
    public static class User {
        private Integer id;
        private String name;
    }
}
