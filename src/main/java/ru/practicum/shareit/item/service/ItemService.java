package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoLastNext;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<ItemDtoLastNext> findAllItemsOwner (Optional<Long> idUser, Optional<Integer> from, Optional<Integer> size)
            throws ValidationException;
    List<ItemDto> findItemSearch (Optional<Long> idUser, String text, Optional<Integer> from, Optional<Integer> size)
            throws ValidationException;
    CommentDto createComment(Optional<Long> idUser, Optional<Long> itemId, CommentDto text) throws ValidationException;
    ItemDtoLastNext findItemById(Optional<Long> idUser, Optional<Long> id) throws ValidationException;
    ItemDto patchItem(Optional<Long> idUser, ItemDto itemDto, Optional<Long> id) throws ValidationException;
    ItemDto createItem(Optional<Long> idUser, ItemDto itemDto) throws ValidationException;
    ItemDto deleteItem(Optional<Long> idUser, Optional<Long> id) throws ValidationException;
}
