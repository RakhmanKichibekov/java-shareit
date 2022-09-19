package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDtoById;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoLastNext {

    public ItemDtoLastNext (Long id, String name, String description, Boolean available){
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoById lastBooking;
    private BookingDtoById nextBooking;
    private List<CommentDto> comments;
    private ItemRequestDto request;

}