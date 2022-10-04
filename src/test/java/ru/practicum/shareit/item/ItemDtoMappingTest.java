package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

public class ItemDtoMappingTest {
    private final ItemMapper itemMapper = new ItemMapper();
    private final Item item = new Item();
    private final ItemDto itemDto = new ItemDto();

    @BeforeEach
    public void init() {
        item.setId(1);
        item.setOwner(1);
        item.setAvailable(true);
        item.setName("item");
        item.setDescription("descr of item");

        itemDto.setId(1);
        itemDto.setOwner(1);
        itemDto.setAvailable(true);
        itemDto.setName("item");
        itemDto.setDescription("descr of item");
    }

    @Test
    public void toDtoFromItemTest() {
        ItemDto result = itemMapper.toDto(item);
        Assertions.assertEquals(result, itemDto);
    }

    @Test
    public void toItemFromDtoTest() {
        Item result = itemMapper.toItem(itemDto, 1, 1);
        Assertions.assertEquals(result, item);
    }
}
