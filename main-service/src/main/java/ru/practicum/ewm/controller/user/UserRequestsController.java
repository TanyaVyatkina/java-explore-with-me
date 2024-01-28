package ru.practicum.ewm.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.user.UserRequestsService;
import ru.practicum.ewm.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserRequestsController {
    private final UserRequestsService userRequestsService;

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable int userId) {
        log.debug("Пришел запрос на получение всех запросов пользователя {}.", userId);
        List<ParticipationRequestDto> foundRequests = userRequestsService.getUserRequests(userId);
        log.debug("Найдены запросы: {}", foundRequests);
        return foundRequests;
    }

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto addUserRequest(@PathVariable int userId, @RequestParam int eventId) {
        log.debug("Пришел запрос на добавление пользователя {} на участие в событии {}.", userId, eventId);
        ParticipationRequestDto addedEvent = userRequestsService.addUserRequest(userId, eventId);
        log.debug("Запрос добавлен.");
        return addedEvent;
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelUserRequest(@PathVariable int userId, @RequestParam int requestId) {
        log.debug("Пришел запрос на отмену запроса участия в событии {} от пользователя {}.", requestId, userId);
        ParticipationRequestDto canceledRequest = userRequestsService.cancelUserRequest(userId, requestId);
        log.debug("Заявка отменена.");
        return canceledRequest;
    }
}