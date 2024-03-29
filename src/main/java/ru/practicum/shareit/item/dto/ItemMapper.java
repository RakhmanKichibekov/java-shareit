package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

@Service
public class ItemMapper {
    public ItemDto toDto(Item item) {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());

        itemDto.setName(item.getName());

        itemDto.setDescription(item.getDescription());

        itemDto.setAvailable(item.getAvailable());

        itemDto.setOwner(item.getOwner());

        itemDto.setRequestId(item.getRequestId());


        return itemDto;
    }

    public Item toItem(ItemDto itemDto, Integer ownerId, Integer id) {
        Item item = new Item();

        item.setId(id);

        item.setName(itemDto.getName());

        item.setDescription(itemDto.getDescription());

        item.setAvailable(itemDto.getAvailable());

        item.setOwner(ownerId);

        item.setRequestId(itemDto.getRequestId());

        return item;
    }
}
