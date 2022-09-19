package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                UserMapper.toUserDto(item.getOwner()),
                item.getRequest() != null ? item.getRequest().getId() : item.getId()
        );
    }
    public static List<CommentDto> toListCommentDto (List<Comment> listComment) {
        List<CommentDto> listDto = new ArrayList<>();
        for (Comment comment : listComment) {
            listDto.add(toCommentDto(comment));
        }
        return listDto;
    }
    public static Item toItem(ItemDto itemDto) {
        return new Item (
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }

    public static List<ItemDto> toListItemDto (List<Item> listItem) {
        List<ItemDto> listDto = new ArrayList<>();
        for (Item item : listItem) {
            listDto.add(toItemDto(item));
        }
        return listDto;
    }

    public static ItemDtoLastNext toItemDtoLastNext(Item item) {
        return new ItemDtoLastNext(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }

    public static List<CommentDto> toListItemDtoLastNext(List<Comment> comments) {
        List<CommentDto> listCommentDto = new ArrayList<>();
        for (Comment comment : comments) {
            listCommentDto.add(toCommentDto(comment));
        }
        return listCommentDto;
    }

    public static Comment toComment(CommentDto commentDto) {
        return new Comment (
                commentDto.getText()
        );
    }
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto (
                comment.getId(),
                comment.getText(),
                toItemDto(comment.getItem()),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

}
