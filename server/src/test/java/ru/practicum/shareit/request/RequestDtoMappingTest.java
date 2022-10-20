package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.requests.dto.ItemRequestDto;
import ru.practicum.shareit.item.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.item.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.Set;

public class RequestDtoMappingTest {
    private final User user1 = new User();
    private final Item item1 = new Item();
    private final Item item2 = new Item();

    private final ItemRequest request1 = new ItemRequest();
    private final ItemRequestDto dto = new ItemRequestDto();

    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();

    @BeforeEach
    public void init() {
        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("u1@user.com");

        item1.setId(1);
        item1.setName("item1");
        item1.setDescription("descr item1");
        item1.setAvailable(true);
        item1.setOwner(user1.getId());
        item1.setRequestId(1);
        item2.setId(2);
        item2.setName("item2");
        item2.setDescription("descr item2");
        item2.setAvailable(true);
        item2.setOwner(user1.getId());
        item2.setRequestId(1);

        request1.setDescription("descr about the item1");
        request1.setRequestor(user1);
        request1.setCreated(LocalDateTime.now().withNano(0));
        request1.setId(1);
        request1.setItems(Set.of(item1, item2));

        dto.setId(1);
        dto.setDescription("descr about the item1");
        dto.setRequestor(user1);
        dto.setCreated(LocalDateTime.now().withNano(0));
        dto.setItems(Set.of(item1, item2));
    }

    @Test
    public void toDtoFromRequestTest() {
        ItemRequestDto result = itemRequestMapper.toDto(request1);
        Assertions.assertEquals(result, dto);
    }

    @Test
    public void toRequestFromDtoTest() {
        ItemRequest result = itemRequestMapper.toItemRequest(dto, user1.getId());
        Assertions.assertNull(result.getId());
        Assertions.assertEquals(result.getDescription(), request1.getDescription());
        Assertions.assertEquals(result.getRequestor().getId(), request1.getRequestor().getId());
    }
}
