package ru.practicum.ewm.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> searchEvents(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd, Boolean onlyAvailable, PageRequest page);

    EventFullDto getEvent(int id);

    List<EventFullDto> searchEventsByAdmin(List<Integer> users, List<EventState> states, List<Integer> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest page);

    EventFullDto updateEvent(int eventId, UpdateEventAdminRequest request);

    List<EventShortDto> getUserEvents(int userId, PageRequest page);

    EventFullDto addUserEvent(int userId, NewEventDto eventDto);

    EventFullDto getUserEvent(int userId, int eventId);

    EventFullDto updateUserEvent(int userId, int eventId, UpdateEventUserRequest request);

    List<ParticipationRequestDto> getUserEventRequests(int userId, int eventId);

    EventRequestStatusUpdateResult updateRequestsStatuses(int userId, int eventId, EventRequestStatusUpdateRequest request);

}
