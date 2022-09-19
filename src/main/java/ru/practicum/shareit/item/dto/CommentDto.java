package ru.practicum.shareit.item.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;
    @NonNull
    private String text;
    private ItemDto item;
    private String authorName;
    private LocalDateTime created;
}
