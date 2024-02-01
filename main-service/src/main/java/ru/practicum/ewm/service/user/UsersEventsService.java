package ru.practicum.ewm.service.user;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.dto.*;

import java.util.List;

public interface UsersEventsService {
    List<EventShortDto> getUserEvents(int userId, PageRequest page);

    EventFullDto addUserEvent(int userId, NewEventDto eventDto);

    EventFullDto getUserEvent(int userId, int eventId);

    EventFullDto updateUserEvent(int userId, int eventId, UpdateEventUserRequest request);

    List<ParticipationRequestDto> getUserEventRequests(int userId, int eventId);

    EventRequestStatusUpdateResult updateRequestsStatuses(int userId, int eventId, EventRequestStatusUpdateRequest request);
}
