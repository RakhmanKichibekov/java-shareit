package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
public class ItemMapper {

    public static ItemDto toItemDto(@NotNull Item item) {
        Integer requestId;
        if (item.getRequest() != null) {
            requestId = item.getRequest().getId();
        } else
            requestId = null;
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                new ItemDto.User(item.getOwner().getId(),
                        item.getOwner().getName()),
                requestId,
                null,
                null,
                null);
    }

    public static Item toItem(@NotNull ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                null
        );
    }
}
