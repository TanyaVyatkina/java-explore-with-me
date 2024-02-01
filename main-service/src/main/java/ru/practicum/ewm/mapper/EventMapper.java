package ru.practicum.ewm.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.client.ViewStatsRequest;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatsClient statsClient;

    public EventShortDto toShortDto(Event event) {
        EventShortDto shortDto = toShortDtoWithoutViews(event);
        List<ViewStats> viewStats = findViews(new String[]{"/events/" + event.getId()});
        if (!viewStats.isEmpty()) {
            shortDto.setViews(viewStats.get(0).getHits());
        }
        return shortDto;
    }

    public EventFullDto toFullDto(Event event) {
        EventFullDto fullDto = toFullDtoWithoutViews(event);
        List<ViewStats> viewStats = findViews(new String[]{"/events/" + event.getId()});
        if (!viewStats.isEmpty()) {
            fullDto.setViews(viewStats.get(0).getHits());
        }
        return fullDto;
    }

    public List<EventShortDto> toShortDtoList(Collection<Event> events) {
        List<EventShortDto> dtos = events.stream()
                .map(this::toShortDtoWithoutViews)
                .collect(Collectors.toList());
        fillViewsToEventShortDtoList(dtos);
        return dtos;
    }

    public List<EventFullDto> toFullDtoList(Collection<Event> events) {
        List<EventFullDto> dtos = events.stream()
                .map(this::toFullDtoWithoutViews)
                .collect(Collectors.toList());
        fillViewsToEventFullDtoList(dtos);
        return dtos;
    }

    public Event toNewEntity(NewEventDto dto, User initiator, Category category) {
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

    private EventShortDto toShortDtoWithoutViews(Event event) {
        EventShortDto shortDto = new EventShortDto(event.getId(), event.getAnnotation(),
                event.getEventDate(), event.isPaid(), event.getTitle());
        shortDto.setCategory(CategoryMapper.toDto(event.getCategory()));
        shortDto.setInitiator(UserMapper.toShortDto(event.getInitiator()));
        shortDto.setConfirmedRequests(findConfirmedRequestCount(event.getId()));
        return shortDto;
    }

    private EventFullDto toFullDtoWithoutViews(Event event) {
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
        fullDto.setConfirmedRequests(findConfirmedRequestCount(event.getId()));
        return fullDto;
    }

    private int findConfirmedRequestCount(int eventId) {
        return participationRequestRepository.countAllByEvent_IdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED);
    }

    private void fillViewsToEventShortDtoList(List<EventShortDto> events) {
        Map<String, EventShortDto> eventsMap = events
                .stream()
                .collect(Collectors.toMap(e -> "/events/" + e.getId(), Function.identity()));
        List<ViewStats> viewStats = findViews(eventsMap.keySet().toArray(new String[eventsMap.size()]));
        for (ViewStats vs : viewStats) {
            eventsMap.get(vs.getUri()).setViews(vs.getHits());
        }
    }

    private void fillViewsToEventFullDtoList(List<EventFullDto> events) {
        Map<String, EventFullDto> eventsMap = events
                .stream()
                .collect(Collectors.toMap(e -> "/events/" + e.getId(), Function.identity()));
        List<ViewStats> viewStats = findViews(eventsMap.keySet().toArray(new String[eventsMap.size()]));
        for (ViewStats vs : viewStats) {
            eventsMap.get(vs.getUri()).setViews(vs.getHits());
        }
    }

    private List<ViewStats> findViews(String[] uris) {
        ViewStatsRequest request = new ViewStatsRequest(LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.now(), uris, true);
        return statsClient.findStatistic(request);
    }
}
