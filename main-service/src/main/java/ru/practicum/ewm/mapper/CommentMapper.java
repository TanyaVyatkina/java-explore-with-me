package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.NewCommentDto;
import ru.practicum.ewm.entity.Comment;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(),
                comment.getCreated(), comment.getUpdated());
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> comments) {
        return comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public static Comment toNewComment(NewCommentDto dto, User author, Event event) {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setText(dto.getText());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }
}

