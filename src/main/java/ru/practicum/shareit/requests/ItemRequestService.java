package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

public interface ItemRequestService {
    List<ItemRequestDto> findAllItemRequest(Optional<Long> idUser) throws ValidationException;

    ItemRequestDto createItemRequest(Optional<Long> idUser, ItemRequestDto itemRequestDto) throws ValidationException;


    List<ItemRequestDto> findItemRequestPageable(Optional<Long> idUser, Optional<Integer> from, Optional<Integer> size)
            throws ValidationException;

    ItemRequestDto findItemRequestById(Optional<Long> idUser, Optional<Long> id) throws ValidationException;
}