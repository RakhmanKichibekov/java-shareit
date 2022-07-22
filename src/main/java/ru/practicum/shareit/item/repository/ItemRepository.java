package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemRepository {
    Map<Integer, Item> getRepository();

    List<Item> findAllByUserId(Integer userId);

    Item findById(Integer id);

    List<Item> search(String text);

    Item add(Item item);

    Item change(Item item);

    Item deleteById(Integer id);
}
