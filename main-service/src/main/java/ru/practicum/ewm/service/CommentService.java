package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.NewCommentDto;

public interface CommentService {
    CommentDto addComment(int userId, int eventId, NewCommentDto commentDto);

    CommentDto updateComment(int userId, int commentId, NewCommentDto commentDto);

    void deleteComment(boolean isAdmin, Integer userId, int commentId);
}
