package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.AccessIsDeniedException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;


    @Override
    public List<ItemDto> findAllByUserId(Integer userId) {
        validateId(userId);
        List<ItemDto> list = new ArrayList<>();
        for (Item item : itemRepository.findAllByUserId(userId)) {
            list.add(ItemMapper.toItemDto(item));
        }
        return list;
    }

    @Override
    public ItemDto findById(Integer id) {
        if (!itemRepository.getRepository().containsKey(id)) {
            log.warn("Вещь с идентификатором {} не найдена.", id);
            throw new ItemNotFoundException("Вещь с id " + id + " не найдена.");
        }
        return ItemMapper.toItemDto(itemRepository.findById(id));
    }

    @Override
    public List<ItemDto> search(String text) {
        List<ItemDto> list = new ArrayList<>();
        if (!text.isBlank()) {
            for (Item item : itemRepository.search(text)) {
                list.add(ItemMapper.toItemDto(item));
            }
        }
        return list;
    }

    @Override
    public ItemDto add(Integer userId, ItemDto itemDto) {
        validateId(userId);
        itemDto.setOwner(userId);
        return ItemMapper.toItemDto(itemRepository.add(ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto change(Integer userId, Integer itemId, ItemDto itemDto) {
        validate(userId, itemId);
        ItemDto oldItem = ItemMapper.toItemDto(itemRepository.getRepository().get(itemId));
        itemDto.setId(itemId);
        itemDto.setOwner(oldItem.getOwner());
        if (itemDto.getName() == null) {
            itemDto.setName(oldItem.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(oldItem.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(oldItem.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.change(ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto deleteById(Integer userId, Integer id) {
        validate(userId, id);
        return ItemMapper.toItemDto(itemRepository.deleteById(id));
    }

    private void validate(Integer userId, Integer id) {
        if (userService.findById(userId) == null) {
            log.warn("Пользователь с идентификатором {} не найден.", userId);
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (!itemRepository.getRepository().containsKey(id)) {
            log.warn("Вещь с идентификатором {} не найдена.", id);
            throw new ItemNotFoundException("Вещь с id " + id + " не найдена.");
        }
        if (!userId.equals(findById(id).getOwner())) {
            log.warn("Редактирование вещи с id {} пользователем с id {}", id, userId);
            throw new AccessIsDeniedException("Недостаточно прав для выполнения операции.");
        }
    }

    private void validateId(Integer userId) {
        if (userService.findById(userId) == null) {
            log.warn("Пользователь с идентификатором {} не найден.", userId);
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }
    }
}