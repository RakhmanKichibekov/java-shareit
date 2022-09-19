package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoLastNext;
import ru.practicum.shareit.item.service.ItemService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemController {
    ItemService itemService;
    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @RequestMapping(value ="/", produces = "application/json")
    public String getURLValue(HttpServletRequest request){
        String test = request.getRequestURI();
        return test;
    }
    @GetMapping()
    protected List<ItemDtoLastNext> findAllItems(@RequestHeader("X-Sharer-User-Id") Optional<Long> idUser,
                                                 @RequestParam(value = "from", required = false) Optional<Integer> from,
                                                 @RequestParam(value = "size", required = false) Optional<Integer> size) throws ValidationException {
        return itemService.findAllItemsOwner(idUser, from, size);
    }

    @GetMapping("/{id}")
    protected ItemDtoLastNext findItemById(@RequestHeader("X-Sharer-User-Id") Optional<Long> idUser,
                                           @PathVariable Optional<Long> id) throws ValidationException {
        return itemService.findItemById(idUser, id);
    }
    @PatchMapping("/{id}")
    protected ItemDto put(@RequestHeader("X-Sharer-User-Id") Optional<Long> idUser,
                          @PathVariable Optional<Long> id, @RequestBody ItemDto itemDto) throws ValidationException {
        return itemService.patchItem (idUser, itemDto, id);
    }

    @DeleteMapping("/{id}")
    protected ItemDto deleteItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> idUser,
                                 @PathVariable Optional<Long> id) throws ValidationException {
        return itemService.deleteItem(idUser, id);
    }

    @PostMapping()
    protected ItemDto createItem(@Valid @RequestHeader("X-Sharer-User-Id") Optional<Long> idUser,
                                 @RequestBody ItemDto itemDto) throws ValidationException {
        return itemService.createItem(idUser, itemDto);
    }

    @GetMapping("/search")
    protected List<ItemDto> findItemById(@RequestHeader("X-Sharer-User-Id") Optional<Long> idUser,
                                         @RequestParam("text") String text,
                                         @RequestParam(value = "from", required = false) Optional<Integer> from,
                                         @RequestParam(value = "size", required = false) Optional<Integer> size
    ) throws ValidationException {
        return itemService.findItemSearch(idUser, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    protected CommentDto create(@Valid @RequestHeader("X-Sharer-User-Id") Optional<Long> idUser,
                                @PathVariable Optional<Long> itemId, @RequestBody CommentDto commentDto) throws ValidationException {
        return itemService.createComment(idUser, itemId, commentDto);
    }
}