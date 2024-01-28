package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.client.ViewStatsRequest;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.EventsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class EventsController {
    private final StatsClient statsClient;
    private final EventsService eventsService;

    @GetMapping
    public List<EventShortDto> searchEvents(@RequestParam String text, @RequestParam Integer[] categories,
                                            @RequestParam Boolean paid, @RequestParam LocalDateTime rangeStart,
                                            @RequestParam LocalDateTime rangeEnd,
                                            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                            @RequestParam String sortBy,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        log.debug("Пришел запрос на поиск событий.");
        List<EventShortDto> foundEvents;
        try {
            PageRequest page = PageRequest.of(from / size, size);
            foundEvents = eventsService.searchEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                    SortType.valueOf(sortBy.toUpperCase()), page);
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
        EventFullDto foundEvent = eventsService.getEvent(id);
        log.debug("Найдено событие: ", foundEvent);
        addToStatistic("/event/" + id, request.getRemoteAddr(), request.getRequestURI());
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
