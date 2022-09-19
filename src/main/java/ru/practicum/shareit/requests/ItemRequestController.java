package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @RequestMapping(value ="/",produces = "application/json")
    public String getURLValue(HttpServletRequest request){
        String test = request.getRequestURI();
        return test;
    }

    @PostMapping()
    protected ItemRequestDto create( @RequestHeader("X-Sharer-User-Id") Optional<Long> idUser,
                                     @Valid @RequestBody ItemRequestDto itemRequestDto) throws ValidationException {
        return itemRequestService.createItemRequest(idUser, itemRequestDto);
    }
    @GetMapping()
    protected List<ItemRequestDto> findAll(@RequestHeader("X-Sharer-User-Id") Optional<Long> idUser) throws ValidationException {
        return itemRequestService.findAllItemRequest(idUser);
    }

    @GetMapping("/all")
    protected List<ItemRequestDto> findItemRequest(@RequestHeader("X-Sharer-User-Id") Optional<Long> idUser,
                                                   @RequestParam(value = "from", required = false) Optional<Integer> from,
                                                   @RequestParam(value = "size", required = false) Optional<Integer> size)
            throws ValidationException {
        return itemRequestService.findItemRequestPageable(idUser, from, size);
    }
    @GetMapping("/{requestId}")
    protected ItemRequestDto findItemRequest(@RequestHeader("X-Sharer-User-Id") Optional<Long> idUser,
                                             @PathVariable Optional<Long> requestId) throws ValidationException {
        return itemRequestService.findItemRequestById(idUser, requestId);
    }

}
