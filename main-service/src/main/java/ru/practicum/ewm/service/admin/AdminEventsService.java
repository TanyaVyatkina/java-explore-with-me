package ru.practicum.ewm.service.admin;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventState;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventsService {
    List<EventFullDto> searchEvents(List<Integer> users, List<EventState> states, List<Integer> categories,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest page);

    EventFullDto updateEvent(int eventId, UpdateEventAdminRequest request);
}
