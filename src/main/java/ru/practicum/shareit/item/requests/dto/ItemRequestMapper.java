package ru.practicum.shareit.item.requests.dto;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Service
public class ItemRequestMapper {

    public ItemRequestDto toDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestor(itemRequest.getRequestor());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(itemRequest.getItems());
        return itemRequestDto;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, Integer requestorId) {
        ItemRequest itemRequest = new ItemRequest();
        User user = new User();
        user.setId(requestorId);
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(user);
        return itemRequest;
    }
}
