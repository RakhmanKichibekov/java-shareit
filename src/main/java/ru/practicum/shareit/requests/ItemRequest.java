package ru.practicum.shareit.requests;

/**
 * // TODO .
 */

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ItemRequest {

    private Integer id;
    private String description;
    private Integer requestor;
    private LocalDate created;

    public ItemRequest(Integer id, String description, Integer requestor, LocalDate created) {
        this.id = id;
        this.description = description;
        this.requestor = requestor;
        this.created = created;
    }
}
