package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.requests.ItemRequest;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
public class ItemRequestMapper {


    public static ItemRequestDto toItemRequestDto(@NotNull ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor(),
                itemRequest.getCreated()

        );
    }

    public static ItemRequest toItemRequest(@NotNull ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequestor(),
                itemRequestDto.getCreated()
        );
    }
}
