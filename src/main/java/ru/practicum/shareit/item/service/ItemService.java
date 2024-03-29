package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoDate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

public interface ItemService {
    Item add(Item item);

    Item update(Item item, Integer userId);

    Item getById(Integer id);

    Collection<ItemDtoDate> getAll(Integer userId, Integer page, Integer size);

    Collection<Item> getByNameOrDesc(String text, Integer page, Integer size);

    void delete(Integer itemId, Integer userId);

    ItemDtoDate getItemDate(Integer itemId, LocalDateTime dateTime, Integer userId);

    CommentDto addComment(CommentDto commentDto, Integer itemId, Integer userId);

    User getUser(Integer id);

}
