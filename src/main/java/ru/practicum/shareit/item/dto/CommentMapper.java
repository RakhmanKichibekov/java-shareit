package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

@Service
public class CommentMapper {

    public CommentDto toDto(Comment comment, User user) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItem(comment.getItem());
        commentDto.setAuthor(comment.getAuthor());
        commentDto.setAuthorName(user.getName());
        return commentDto;
    }

    public Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(commentDto.getItem());
        comment.setAuthor(commentDto.getAuthor());
        return comment;
    }
}