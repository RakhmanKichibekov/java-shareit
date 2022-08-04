package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

import javax.validation.constraints.NotNull;

public class CommentMapper {

    public static CommentDto toCommentDto(@NotNull Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getCreated(),
                comment.getAuthor().getName(),
                new CommentDto.Item(comment.getItem().getId(),
                        comment.getItem().getName()));
    }

}
