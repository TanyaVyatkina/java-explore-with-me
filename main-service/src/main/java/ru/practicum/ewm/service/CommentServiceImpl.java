package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.entity.Comment;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public CommentDto addComment(int userId, int eventId, NewCommentDto commentDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие id = " + eventId));
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new NotFoundException("Событие должно быть опубликовано.");
        }
        int userRequestCount = participationRequestRepository.countAllByRequester_IdAndEventIdAndStatus(userId,
                eventId, ParticipationRequestStatus.CONFIRMED);
        if (userRequestCount == 0) {
            throw new ConflictException("Оставлять комментарии может только участник данного события.");
        }
        User author = userRepository.findById(userId).get();
        Comment comment = CommentMapper.toNewComment(commentDto, author, event);
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(int userId, int commentId, NewCommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Не найден комментарий с id = " + commentId));
        if (!EventState.PUBLISHED.equals(comment.getEvent().getState())) {
            throw new NotFoundException("Событие должно быть опубликовано.");
        }
        if (userId != comment.getAuthor().getId()) {
            throw new ValidationException("Изменить комментарий может только пользователь, который его добавил.");
        }
        comment.setText(commentDto.getText());
        comment.setUpdated(LocalDateTime.now());
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(DeleteCommentRequest request) {
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new NotFoundException("Не найден комментарий с id = " + request.getCommentId()));
        if (!EventState.PUBLISHED.equals(comment.getEvent().getState())) {
            throw new NotFoundException("Событие должно быть опубликовано.");
        }
        if (!request.isAdmin() && !request.getUserId().equals(comment.getAuthor().getId())) {
            throw new ValidationException("Удалить комментарий может только пользователь, который его добавил, " +
                    "или администратор.");
        }
        commentRepository.delete(comment);
    }
}
