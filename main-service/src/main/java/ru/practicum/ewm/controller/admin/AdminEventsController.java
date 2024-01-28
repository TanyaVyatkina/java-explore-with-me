package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.service.admin.AdminEventsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class AdminEventsController {
    private final AdminEventsService adminEventsService;

    @GetMapping
    public List<EventFullDto> searchEvents(@RequestParam int[] users,
                                           @RequestParam String[] states,
                                           @RequestParam int[] categories,
                                           @RequestParam LocalDateTime rangeStart,
                                           @RequestParam LocalDateTime rangeEnd,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        log.debug("Пришел запрос на поиск событий.");
        PageRequest page = PageRequest.of(from / size, size);
        List<EventFullDto> foundEvents = adminEventsService.searchEvents(users, states, categories, rangeStart,
                rangeEnd, page);
        log.debug("Найдены события: {}.", foundEvents);
        return foundEvents;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable int eventId, @RequestBody UpdateEventAdminRequest request) {
        log.debug("Пришел запрос на изменение события.");
        EventFullDto updatedEvent = adminEventsService.updateEvent(eventId, request);
        log.debug("Изменения сохранены.");
        return updatedEvent;
    }
}
