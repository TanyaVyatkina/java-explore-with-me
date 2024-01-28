package ru.practicum.ewm.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.user.UsersEventsService;
import ru.practicum.ewm.dto.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UsersEventsController {
    private final UsersEventsService usersEventsService;
    @GetMapping("/{userId}/events")
    public List<EventShortDto> getUserEvents(@PathVariable int userId, @RequestParam int from, @RequestParam int size) {
        log.debug("Пришел запрос на получение событий, добавленных пользователем: {}.", userId);
        PageRequest page = PageRequest.of(from / size, size);
        List<EventShortDto> foundEvents = usersEventsService.getUserEvents(userId, page);
        log.debug("Найдены события: {}", foundEvents);
        return foundEvents;
    }

    @PostMapping("/{userId}/events")
    public EventShortDto addUserEvent(@PathVariable int userId, @RequestBody NewEventDto eventDto) {
        log.debug("Пришел запрос на добавление события пользователем: {}.", userId);
        EventShortDto addedEvent = usersEventsService.addUserEvent(userId, eventDto);
        log.debug("Добавлено событие: {}", addedEvent);
        return addedEvent;
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUserEvent(@PathVariable int userId, @PathVariable int eventId) {
        log.debug("Пришел запрос на получение события {} пользователя  {}.", eventId, userId);
        EventFullDto foundEvent = usersEventsService.getUserEvent(userId, eventId);
        log.debug("Найдено событие: {}", foundEvent);
        return foundEvent;
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateUserEvent(@PathVariable int userId, @PathVariable int eventId,
                                     @RequestBody UpdateEventUserRequest request) {
        log.debug("Пришел запрос на изменение события {} пользователя  {}.", eventId, userId);
        EventFullDto foundEvent = usersEventsService.updateUserEvent(userId, eventId, request);
        log.debug("Событие изменено.");
        return foundEvent;
    }
    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequests(@PathVariable int userId, @PathVariable int eventId) {
        log.debug("Пришел запрос на получение на получение информации о запросах на участие в событии {} " +
                "пользователя {}.", eventId, userId);
        List<ParticipationRequestDto> foundRequests = usersEventsService.getUserEventRequests(userId, eventId);
        log.debug("Найдены запросы: {}", foundRequests);
        return foundRequests;
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsStatuses(@PathVariable int userId, @PathVariable int eventId,
                                        @RequestBody EventRequestStatusUpdateRequest request) {
        log.debug("Пришел запрос на изменение статуса заявок события {} пользователя  {}.", eventId, userId);
        EventRequestStatusUpdateResult updateRequestsResult = usersEventsService.updateRequestsStatuses(userId, eventId, request);
        log.debug("Событие изменено.");
        return updateRequestsResult;
    }
}
