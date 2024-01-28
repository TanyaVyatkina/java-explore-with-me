package ru.practicum.ewm.service.admin;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventsService {
    List<EventFullDto> searchEvents(int[] users, String[] states, int[] categories, LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd, PageRequest page);
    EventFullDto updateEvent(int eventId, UpdateEventAdminRequest request);
}
