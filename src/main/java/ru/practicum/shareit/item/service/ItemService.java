package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> findAllByUserId(Integer userId);

    ItemDto findById(Integer id);

    List<ItemDto> search(String text);

    ItemDto add(Integer userId, ItemDto itemDto);

    ItemDto change(Integer userId, Integer itemId, ItemDto itemDto);

    ItemDto deleteById(Integer userId, Integer id);
}
