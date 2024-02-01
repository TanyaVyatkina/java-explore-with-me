package ru.practicum.ewm.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventState;
import ru.practicum.ewm.dto.SortType;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.QEvent;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public List<EventFullDto> searchEvents(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Boolean onlyAvailable, SortType sortBy, int from, int size) {
        QEvent event = QEvent.event;
        List<BooleanExpression> queryConditions = new ArrayList<>();
        if (text != null) {
            queryConditions.add(event.description.likeIgnoreCase(text).or(event.annotation.likeIgnoreCase(text)));
        }
        if (categories != null) {
            queryConditions.add(event.category.id.in(categories));
        }
        if (paid != null) {
            queryConditions.add(event.paid.eq(paid));
        }
        queryConditions.add(event.eventDate.after(rangeStart));
        if (rangeEnd != null) {
            queryConditions.add(event.eventDate.before(rangeEnd));
        }
        BooleanExpression conditions = Expressions.allOf(queryConditions.toArray(new BooleanExpression[queryConditions.size()]));

        List<Event> events = (ArrayList<Event>) eventRepository.findAll(conditions);
        List<EventFullDto> foundEvents = eventMapper.toFullDtoList(events)
                .stream().filter(e -> {
                    if (onlyAvailable) {
                        return e.getParticipantLimit() == 0 || e.getConfirmedRequests() < e.getParticipantLimit();
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
        if (SortType.EVENT_DATE.equals(sortBy)) {
            return foundEvents.stream()
                    .sorted(EventsServiceImpl::compareByEventDate)
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        } else {
            return foundEvents.stream()
                    .sorted(EventsServiceImpl::compareByViews)
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public EventFullDto getEvent(int id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с id = " + id));
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new NotFoundException("Событие должно быть опубликовано.");
        }
        return eventMapper.toFullDto(event);
    }

    public static int compareByViews(EventFullDto e1, EventFullDto e2) {
        return e2.getViews() > e1.getViews() ? 1 : -1;
    }

    public static int compareByEventDate(EventFullDto e1, EventFullDto e2) {
        return e2.getEventDate().compareTo(e1.getEventDate());
    }
}
