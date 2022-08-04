package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {

    @Data
    @AllArgsConstructor
    public static class Item {
        private Integer id;
        private String name;
    }

    @Null
    private Integer id;
    @NotBlank
    private String text;
    private LocalDateTime created;
    private String authorName;
    private CommentDto.Item item;

}
