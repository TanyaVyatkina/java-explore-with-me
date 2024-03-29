package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.DeleteCommentRequest;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventState;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.service.CommentService;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService eventService;
    private final CommentService commentService;

    @GetMapping
    public List<EventFullDto> searchEvents(@RequestParam(required = false) List<Integer> users,
                                           @RequestParam(required = false) List<EventState> states,
                                           @RequestParam(required = false) List<Integer> categories,
                                           @RequestParam(required = false) String rangeStart,
                                           @RequestParam(required = false) String rangeEnd,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        log.debug("Пришел запрос на поиск событий.");
        PageRequest page = PageRequest.of(from / size, size);
        LocalDateTime start = null;
        LocalDateTime end = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (rangeStart != null) {
            start = LocalDateTime.parse(URLDecoder.decode(rangeStart), formatter);
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(URLDecoder.decode(rangeEnd), formatter);
        }
        List<EventFullDto> foundEvents = eventService.searchEventsByAdmin(users, states, categories, start,
                end, page);
        log.debug("Найдены события: {}.", foundEvents);
        return foundEvents;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable int eventId, @RequestBody @Valid UpdateEventAdminRequest request) {
        log.debug("Пришел запрос на изменение события.");
        EventFullDto updatedEvent = eventService.updateEvent(eventId, request);
        log.debug("Изменения сохранены.");
        return updatedEvent;
    }

    @DeleteMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable("commentId") int commentId) {
        log.debug("Удаление комментария: {}.", commentId);
        DeleteCommentRequest request = new DeleteCommentRequest(commentId, true, null);
        commentService.deleteComment(request);
        log.debug("Комментарий удален.");
    }
}
