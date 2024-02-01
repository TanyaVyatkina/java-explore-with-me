package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.SortType;
import ru.practicum.ewm.service.EventsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class EventsController {
    private final StatsClient statsClient;
    private final EventsService eventsService;

    @GetMapping
    public List<EventFullDto> searchEvents(@RequestParam(required = false) String text,
                                           @RequestParam(required = false) List<Integer> categories,
                                           @RequestParam(required = false) Boolean paid,
                                           @RequestParam(required = false) String rangeStart,
                                           @RequestParam(required = false) String rangeEnd,
                                           @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                           @RequestParam(defaultValue = "EVENT_DATE") String sortBy,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        log.debug("Пришел запрос на поиск событий.");
        List<EventFullDto> foundEvents;
        LocalDateTime start = null;
        LocalDateTime end = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (rangeStart != null) {
            start = LocalDateTime.parse(URLDecoder.decode(rangeStart), formatter);
        } else {
            start = LocalDateTime.now();
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(URLDecoder.decode(rangeEnd), formatter);
        }
        if (start != null && end != null && end.isBefore(start)) {
            throw new ValidationException("Дата начала поиска должна быть не позже даты окончания.");
        }
        try {
            foundEvents = eventsService.searchEvents(text, categories, paid, start, end, onlyAvailable,
                    SortType.valueOf(sortBy.toUpperCase()), from, size);
        } catch (IllegalArgumentException ex) {
            log.debug("Неверно указан параметр сортировки.");
            throw new ValidationException("Неверно указан параметр сортировки.");
        }
        log.debug("Найдены события", foundEvents);
        addToStatistic("/events", request.getRemoteAddr(), request.getRequestURI());
        return foundEvents;
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable int id, HttpServletRequest request) {
        log.debug("Пришел запрос на поиск события: {}.", id);
        addToStatistic("/event/" + id, request.getRemoteAddr(), request.getRequestURI());
        EventFullDto foundEvent = eventsService.getEvent(id);
        log.debug("Найдено событие: ", foundEvent);
        return foundEvent;
    }

    private void addToStatistic(String app, String ip, String uri) {
        log.debug("Сохранение статистики app = {}, ip = {}, uri = {} .", app, ip, uri);
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(app);
        endpointHit.setIp(ip);
        endpointHit.setUri(uri);
        endpointHit.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        statsClient.saveEndpoint(endpointHit);
        log.debug("Статистика сохранена.");
    }
}
