package ru.practicum.shareit.requests.dto;
import ru.practicum.shareit.requests.ItemRequest;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor(),
                itemRequest.getCreated()
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getDescription());
    }
    public static List<ItemRequestDto> toListItemRequestDto(List<ItemRequest> listItemRequest) {
        List<ItemRequestDto> listDto = new ArrayList<>();
        for (ItemRequest itemRequest : listItemRequest) {
            listDto.add(toItemRequestDto(itemRequest));
        }
        return listDto;
    }

}
