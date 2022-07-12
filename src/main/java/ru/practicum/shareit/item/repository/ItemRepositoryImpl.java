package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.IdGenerator;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Item> repository = new HashMap<>();

    private final IdGenerator idGenerator = new IdGenerator();


    @Override
    public Map<Integer, Item> getRepository() {
        return repository;
    }

    @Override
    public List<Item> findAllByUserId(Integer userId) {
        List<Item> list = new ArrayList<>();
        for (Item item : repository.values()) {
            if (item.getOwner().equals(userId)) {
                list.add(item);
            }
        }
        return list;
    }

    @Override
    public Item findById(Integer id) {
        return repository.get(id);
    }

    @Override
    public List<Item> search(String text) {
        List<Item> list = new ArrayList<>();
        for (Item item : repository.values()) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    && item.getAvailable()) {
                list.add(item);
            }
        }
        return list;
    }

    @Override
    public Item add(Item item) {
        item.setId(idGenerator.getNextIdCount());
        repository.put(item.getId(), item);
        return item;
    }

    @Override
    public Item change(Item item) {
        repository.put(item.getId(), item);
        return item;
    }

    @Override
    public Item deleteById(Integer id) {
        return repository.remove(id);
    }
}

