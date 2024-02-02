package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EventMapper {
    public static EventShortDto toShortDto(Event event, List<ViewStats> views, int confirmedRequest) {
        EventShortDto shortDto = toShortDtoWithoutViews(event);
        shortDto.setConfirmedRequests(confirmedRequest);
        if (!views.isEmpty()) {
            shortDto.setViews(views.get(0).getHits());
        }
        return shortDto;
    }

    public static EventFullDto toFullDto(Event event, List<ViewStats> views, int confirmedRequest) {
        EventFullDto fullDto = toFullDtoWithoutViews(event);
        fullDto.setConfirmedRequests(confirmedRequest);
        if (!views.isEmpty()) {
            fullDto.setViews(views.get(0).getHits());
        }
        return fullDto;
    }

    public static List<EventShortDto> toShortDtoList(Collection<Event> events,
                                                     List<ViewStats> views,
                                                     List<ParticipationStat> participationStats) {
        List<EventShortDto> dtos = events.stream()
                .map(EventMapper::toShortDtoWithoutViews)
                .collect(Collectors.toList());
        fillViewsToEventShortDtoList(dtos, views);
        fillConfirmedRequestsToEventShortDtoList(dtos, participationStats);
        return dtos;
    }

    public static List<EventFullDto> toFullDtoList(Collection<Event> events, List<ViewStats> views,
                                                   List<ParticipationStat> participationStats) {
        List<EventFullDto> dtos = events.stream()
                .map(EventMapper::toFullDtoWithoutViews)
                .collect(Collectors.toList());
        fillViewsToEventFullDtoList(dtos, views);
        fillConfirmedRequestsToEventFullDtoList(dtos, participationStats);
        return dtos;
    }

    public static Event toNewEntity(NewEventDto dto, User initiator, Category category) {
        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLon(dto.getLocation().getLon());
        event.setLat(dto.getLocation().getLat());
        event.setCreatedOn(LocalDateTime.now());
        event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        } else {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getPaid() == null) {
            dto.setPaid(false);
        } else {
            event.setPaid(dto.getPaid());
        }
        event.setTitle(dto.getTitle());
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setState(EventState.PENDING);
        return event;
    }

    private static EventShortDto toShortDtoWithoutViews(Event event) {
        EventShortDto shortDto = new EventShortDto(event.getId(), event.getAnnotation(),
                event.getEventDate(), event.isPaid(), event.getTitle());
        shortDto.setCategory(CategoryMapper.toDto(event.getCategory()));
        shortDto.setInitiator(UserMapper.toShortDto(event.getInitiator()));
        return shortDto;
    }

    private static EventFullDto toFullDtoWithoutViews(Event event) {
        EventFullDto fullDto = new EventFullDto();
        fullDto.setId(event.getId());
        fullDto.setAnnotation(event.getAnnotation());
        fullDto.setCategory(CategoryMapper.toDto(event.getCategory()));
        fullDto.setEventDate(event.getEventDate());
        fullDto.setCreatedOn(event.getCreatedOn());
        fullDto.setPublishedOn(event.getPublishedOn());
        fullDto.setDescription(event.getDescription());
        fullDto.setLocation(new Location(event.getLat(), event.getLon()));
        fullDto.setInitiator(UserMapper.toShortDto(event.getInitiator()));
        fullDto.setPaid(event.isPaid());
        fullDto.setParticipantLimit(event.getParticipantLimit());
        fullDto.setRequestModeration(event.isRequestModeration());
        fullDto.setState(event.getState());
        fullDto.setTitle(event.getTitle());
        return fullDto;
    }

    private static void fillViewsToEventShortDtoList(List<EventShortDto> events, List<ViewStats> views) {
        Map<String, EventShortDto> eventsMap = events
                .stream()
                .collect(Collectors.toMap(e -> "/events/" + e.getId(), Function.identity()));
        for (ViewStats vs : views) {
            eventsMap.get(vs.getUri()).setViews(vs.getHits());
        }
    }

    private static void fillViewsToEventFullDtoList(List<EventFullDto> events, List<ViewStats> views) {
        Map<String, EventFullDto> eventsMap = events
                .stream()
                .collect(Collectors.toMap(e -> "/events/" + e.getId(), Function.identity()));
        for (ViewStats vs : views) {
            eventsMap.get(vs.getUri()).setViews(vs.getHits());
        }
    }

    private static void fillConfirmedRequestsToEventShortDtoList(List<EventShortDto> events,
                                                                 List<ParticipationStat> participationStats) {
        Map<Integer, EventShortDto> eventsMap = events
                .stream()
                .collect(Collectors.toMap(e -> e.getId(), Function.identity()));
        for (ParticipationStat pr : participationStats) {
            eventsMap.get(pr.getEventId()).setConfirmedRequests(pr.getRequestCount());
        }
    }

    private static void fillConfirmedRequestsToEventFullDtoList(List<EventFullDto> events,
                                                                List<ParticipationStat> participationStats) {
        Map<Integer, EventFullDto> eventsMap = events
                .stream()
                .collect(Collectors.toMap(e -> e.getId(), Function.identity()));
        for (ParticipationStat pr : participationStats) {
            eventsMap.get(pr.getEventId()).setConfirmedRequests(pr.getRequestCount());
        }
    }
}
