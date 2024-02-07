package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.entity.Comment;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventMapper {
    public static EventShortDto toShortDto(Event event, Long views, Long confirmedRequest) {
        EventShortDto shortDto = toShortDtoWithoutViews(event);
        shortDto.setConfirmedRequests(confirmedRequest);
        shortDto.setViews(views);
        return shortDto;
    }

    public static EventFullDto toFullDto(Event event, Long views, Long confirmedRequest, List<CommentDto> comments) {
        EventFullDto fullDto = toFullDtoWithoutViews(event);
        if (views != null) {
            fullDto.setViews(views);
        }
        if (confirmedRequest != null) {
            fullDto.setConfirmedRequests(confirmedRequest);
        }
        fullDto.setComments(comments);
        return fullDto;
    }

    public static List<EventShortDto> toShortDtoList(Collection<Event> events,
                                                     Map<Integer, Long> views,
                                                     Map<Integer, Long> participationStats) {
        List<EventShortDto> dtos = events.stream()
                .map(event -> {
                    EventShortDto shortDto = toShortDtoWithoutViews(event);
                    int eventId = event.getId();
                    if (participationStats.get(eventId) != null)
                        shortDto.setConfirmedRequests(participationStats.get(eventId));
                    if (views.get(eventId) != null)
                        shortDto.setViews(views.get(eventId));
                    return shortDto;
                })
                .collect(Collectors.toList());
        return dtos;
    }

    public static List<EventFullDto> toFullDtoList(Collection<Event> events,
                                                   Map<Integer, Long> views,
                                                   Map<Integer, Long> participationStats,
                                                   Map<Event, List<Comment>> comments) {
        List<EventFullDto> dtos = events.stream()
                .map(event -> {
                    EventFullDto fullDto = toFullDtoWithoutViews(event);
                    int eventId = event.getId();
                    if (participationStats.get(eventId) != null)
                        fullDto.setConfirmedRequests(participationStats.get(eventId));
                    if (views.get(eventId) != null)
                        fullDto.setViews(views.get(event.getId()));
                    if (comments.get(event) != null) {
                        fullDto.setComments(CommentMapper.toCommentDtoList(comments.get(event)));
                    }
                    return fullDto;
                })
                .collect(Collectors.toList());
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
}
