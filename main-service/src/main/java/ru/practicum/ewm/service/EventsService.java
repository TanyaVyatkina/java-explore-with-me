package ru.practicum.ewm.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.SortType;

import java.time.LocalDateTime;
import java.util.List;

public interface EventsService {
    List<EventFullDto> searchEvents(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd, Boolean onlyAvailable, SortType sortBy, int from,  int size);

    EventFullDto getEvent(int id);
}
