package ru.practicum.ewm.service.admin;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.AdminStateAction;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventState;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.QEvent;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminEventsServiceImpl implements AdminEventsService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    @Override
    public List<EventFullDto> searchEvents(List<Integer> users, List<EventState> states, List<Integer> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest page) {
        QEvent event = QEvent.event;
        List<BooleanExpression> queryConditions = new ArrayList<>();
        if (users != null) {
            queryConditions.add(event.initiator.id.in(users));
        }
        if (states != null) {
            queryConditions.add(event.state.in(states));
        }
        if (categories != null) {
            queryConditions.add(event.category.id.in(categories));
        }
        if (rangeStart != null) {
            queryConditions.add(event.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            queryConditions.add(event.eventDate.before(rangeEnd));
        }
        BooleanExpression conditions = Expressions.allOf(queryConditions.toArray(new BooleanExpression[queryConditions.size()]));

        if (queryConditions.isEmpty()) {
            return eventMapper.toFullDtoList(eventRepository.findAll(page).getContent());
        }
        return eventMapper.toFullDtoList(eventRepository.findAll(conditions, page).getContent());
    }

    @Override
    public EventFullDto updateEvent(int eventId, UpdateEventAdminRequest request) {
        validateUpdateEventAdminRequest(request);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие id = " + eventId));
        if (AdminStateAction.PUBLISH_EVENT.equals(request.getStateAction())) {
            if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now().plusHours(1))
                    || request.getEventDate() == null && event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ConflictException("Дата начала изменяемого события должна быть не ранее чем за час" +
                        " от даты публикации.");
            } else {
                if (!EventState.PENDING.equals(event.getState())) {
                    throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации.");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
        } else {
            if (EventState.PUBLISHED.equals(event.getState())) {
                throw new ConflictException("Событие можно отклонить, только если оно еще не опубликовано.");
            }
            event.setState(EventState.CANCELED);
        }
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null) {
            event.setLat(request.getLocation().getLat());
            event.setLon(request.getLocation().getLon());
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getCategory() != null) {
            Category newCat = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new ValidationException("Категория с id = " + request.getCategory()
                            + " не найдена или недоступна."));
            event.setCategory(newCat);
        }
        eventRepository.save(event);
        return eventMapper.toFullDto(event);
    }

    private void validateUpdateEventAdminRequest(UpdateEventAdminRequest request) {
        String annotation = request.getAnnotation();
        if (annotation != null && (annotation.length() < 20 || annotation.length() > 2000)) {
            throw new ValidationException("Размер annotation должен находиться от 20 до 2000.");
        }
        String description = request.getDescription();
        if (description != null && (description.length() < 20 || description.length() > 7000)) {
            throw new ValidationException("Размер description должен находиться от 20 до 7000.");
        }
        String title = request.getTitle();
        if (title != null && (title.length() < 3 || title.length() > 120)) {
            throw new ValidationException("Размер title должен находиться от 3 до 120.");
        }
        LocalDateTime eventDate = request.getEventDate();
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата события не может быть в прошлом.");
        }
    }
}
