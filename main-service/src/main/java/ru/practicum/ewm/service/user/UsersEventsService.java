package ru.practicum.ewm.service.user;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.dto.*;

import java.util.List;

public interface UsersEventsService {
    List<EventShortDto> getUserEvents(int userId, PageRequest page);
    EventShortDto addUserEvent(int userId, NewEventDto eventDto);
    EventFullDto getUserEvent(int userId, int eventId);
    EventFullDto updateUserEvent(int userId, int eventId, UpdateEventUserRequest request);
    List<ParticipationRequestDto> getUserEventRequests(int userId, int eventId);
    EventRequestStatusUpdateResult updateRequestsStatuses(int userId, int eventId, EventRequestStatusUpdateRequest request);
}
