package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<ItemDto> findAllByUserId(Integer userId);

    ItemDto findById(Integer userId, Integer itemId);

    ItemDto add(Integer userId, ItemDto itemDto);

    ItemDto change(Integer userId, Integer itemId, ItemDto itemDto);

    ItemDto deleteById(Integer userId, Integer id);

    List<ItemDto> search(String text);

    Item findItemOrException(Integer id);

    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto);

}
