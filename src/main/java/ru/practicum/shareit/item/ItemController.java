package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public ItemDto findById(@PathVariable(value = "id") Integer id) {
        return itemService.findById(id);
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<ItemDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.findAllByUserId(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(OK)
    public List<ItemDto> search(@RequestParam(value = "text") String text) {
        return itemService.search(text);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                       @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(OK)
    public ItemDto change(@PathVariable(value = "id") Integer id,
                          @RequestHeader("X-Sharer-User-Id") Integer userId,
                          @RequestBody ItemDto itemDto) {
        return itemService.change(userId, id, itemDto);
    }

}
