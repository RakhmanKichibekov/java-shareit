package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Getter
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<ItemDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.debug("Найти вещи пользователя {}.", userId);
        return itemService.findAllByUserId(userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                            @PathVariable(value = "id") Integer itemId) {
        log.debug("Найти вещь c id {} пользователем с id {}.", itemId, userId);
        return itemService.findById(userId, itemId);
    }

    @GetMapping("/search")
    @ResponseStatus(OK)
    public List<ItemDto> search(@RequestParam(value = "text") String text) {
        log.debug("Найти список вещей по тексту {}.", text);
        return itemService.search(text);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                       @Valid @RequestBody ItemDto itemDto) {
        log.debug("Добавить вещь с name {} пользователем c id {}.", itemDto.getName(), userId);
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(OK)
    public ItemDto change(@PathVariable(value = "id") Integer id,
                          @RequestHeader("X-Sharer-User-Id") Integer userId,
                          @RequestBody ItemDto itemDto) {
        log.debug("Изменить вещь с id {} пользователем c id {}.", id, userId);
        return itemService.change(userId, id, itemDto);
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @PathVariable Integer itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.debug("Добавить комментарий к вещи с id {} пользователем c id {}.", itemId, userId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}
