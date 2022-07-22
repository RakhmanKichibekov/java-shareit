package ru.practicum.shareit.requests;

import lombok.Data;

import java.time.LocalDate;

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
