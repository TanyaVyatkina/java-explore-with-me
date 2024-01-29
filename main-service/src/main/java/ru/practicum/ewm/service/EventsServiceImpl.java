package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.client.ViewStatsRequest;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService {
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> searchEvents(String text, Integer[] categories, Boolean paid, LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd, Boolean onlyAvailable, SortType sortBy, PageRequest page) {
        List<Event> events;
        if (rangeStart == null && rangeEnd == null) {
            if (onlyAvailable) {
                events = eventRepository.searchOnlyAvailableFutureEvents(text, categories, paid, LocalDateTime.now(), page);
            } else {
                events = eventRepository.searchFutureEvents(text, categories, paid, LocalDateTime.now(), page);
            }
        } else {
            if (onlyAvailable) {
                events = eventRepository.searchOnlyAvailableEventsWithDates(text, categories, paid, rangeStart, rangeEnd, page);
            } else {
                events = eventRepository.searchEventsWithDates(text, categories, paid, rangeStart, rangeEnd, page);
            }
        }
        List<EventShortDto> foundEvents = EventMapper.toShortDtoList(events);
        fillViews(foundEvents);
        if (SortType.EVENT_DATE.equals(sortBy)) {
            return foundEvents.stream().sorted(EventsServiceImpl::compareByEventDate).collect(Collectors.toList());
        } else {
            return foundEvents.stream().sorted(EventsServiceImpl::compareByViews).collect(Collectors.toList());
        }
    }

    @Override
    public EventFullDto getEvent(int id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с id = " + id));
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("Событие должно быть опубликовано.");
        }
        EventFullDto foundEvent = EventMapper.toFullDto(event);
        List<ViewStats> viewStats = findViews(new String[]{"/events" + id});
        foundEvent.setViews(viewStats.get(0).getHits());
        return foundEvent;
    }

    private void fillViews(List<EventShortDto> events) {
        Map<String, EventShortDto> eventsMap = events
                .stream()
                .collect(Collectors.toMap(e -> "/events/" + e.getId(), Function.identity()));
        List<ViewStats> viewStats = findViews(eventsMap.keySet().toArray(new String[eventsMap.size()]));
        for (ViewStats vs : viewStats) {
            eventsMap.get(vs.getApp()).setViews(vs.getHits());
        }
    }

    private List<ViewStats> findViews(String[] uris) {
        ViewStatsRequest request = new ViewStatsRequest(LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.now(), uris, false);
        return statsClient.findStatistic(request);
    }

    public static int compareByViews(EventShortDto e1, EventShortDto e2) {
        return  e2.getViews() > e1.getViews() ? 1 : -1;
    }

    public static int compareByEventDate(EventShortDto e1, EventShortDto e2) {
        return e2.getEventDate().compareTo(e1.getEventDate());
    }
}
