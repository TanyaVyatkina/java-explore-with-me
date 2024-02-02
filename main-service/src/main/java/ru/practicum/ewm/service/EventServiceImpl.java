package ru.practicum.ewm.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.client.ViewStatsRequest;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.entity.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventFullDto> searchEvents(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Boolean onlyAvailable, PageRequest page) {
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

        List<Event> events = eventRepository.findAll(conditions, page).getContent();
        List<ViewStats> views = findViewsForEvents(events);
        List<ParticipationStat> participationStats = findConfirmedRequestCount(events);
        List<EventFullDto> foundEvents = EventMapper.toFullDtoList(events, views, participationStats)
                .stream().filter(e -> {
                    if (onlyAvailable) {
                        return e.getParticipantLimit() == 0 || e.getConfirmedRequests() < e.getParticipantLimit();
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
        if (page.getSort() == null) {
            return foundEvents.stream()
                    .sorted(EventServiceImpl::compareByViews)
                    .collect(Collectors.toList());
        }
        return foundEvents;

    }

    @Override
    public EventFullDto getEvent(int id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с id = " + id));
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new NotFoundException("Событие должно быть опубликовано.");
        }
        List<ViewStats> views = findViewsForEvents(Arrays.asList(event));
        return EventMapper.toFullDto(event, views, findConfirmedRequestCount(id));
    }

    @Override
    public List<EventFullDto> searchEventsByAdmin(List<Integer> users, List<EventState> states, List<Integer> categories,
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
        List<Event> events;
        if (queryConditions.isEmpty()) {
            events = eventRepository.findAll(page).getContent();
        } else {
            events = eventRepository.findAll(conditions, page).getContent();
        }
        List<ViewStats> views = findViewsForEvents(events);
        List<ParticipationStat> participationStats = findConfirmedRequestCount(events);
        return EventMapper.toFullDtoList(events, views, participationStats);
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
        List<ViewStats> views = findViewsForEvents(Arrays.asList(event));
        return EventMapper.toFullDto(event, views, findConfirmedRequestCount(eventId));
    }

    @Override
    public List<EventShortDto> getUserEvents(int userId, PageRequest page) {
        List<Event> events = eventRepository.findByInitiator_Id(userId, page);
        List<ViewStats> views = findViewsForEvents(events);
        List<ParticipationStat> participationStats = findConfirmedRequestCount(events);
        return EventMapper.toShortDtoList(events, views, participationStats);
    }

    @Override
    public EventFullDto addUserEvent(int userId, NewEventDto eventDto) {
        checkData(eventDto.getEventDate());
        User initiator = userRepository.findById(userId).get();
        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new ValidationException("Категория с id = " + eventDto.getCategory()
                        + " не найдена или недоступна."));
        Event event = EventMapper.toNewEntity(eventDto, initiator, category);
        event = eventRepository.save(event);
        return EventMapper.toFullDto(event, Collections.emptyList(), 0);
    }

    @Override
    public EventFullDto getUserEvent(int userId, int eventId) {
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие id = " + eventId
                        + "пользователя id = " + userId));
        List<ViewStats> views = findViewsForEvents(Arrays.asList(event));
        return EventMapper.toFullDto(event, views, findConfirmedRequestCount(eventId));
    }

    @Override
    public EventFullDto updateUserEvent(int userId, int eventId, UpdateEventUserRequest request) {
        validateUpdateEventUserRequest(request);
        checkData(request.getEventDate());

        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с id = " + eventId));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Изменить можно только отмененные события " +
                    "или события в состоянии ожидания модерации.");
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
        if (UserStateAction.SEND_TO_REVIEW.equals(request.getStateAction())) {
            event.setState(EventState.PENDING);
        }
        if (UserStateAction.CANCEL_REVIEW.equals(request.getStateAction())) {
            event.setState(EventState.CANCELED);
        }
        eventRepository.save(event);
        List<ViewStats> views = findViewsForEvents(Arrays.asList(event));
        return EventMapper.toFullDto(event, views, findConfirmedRequestCount(eventId));
    }

    @Override
    public List<ParticipationRequestDto> getUserEventRequests(int userId, int eventId) {
        List<ParticipationRequest> participationRequests = participationRequestRepository.findByEvent_Id(eventId);
        return ParticipationRequestMapper.toDtoList(participationRequests);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatuses(int userId, int eventId, EventRequestStatusUpdateRequest request) {
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с id = " + eventId
                        + "пользователя id = " + userId));
        int participantLimit = event.getParticipantLimit();
        int confirmedRequestsCount = participationRequestRepository.countAllByEvent_IdAndStatus(eventId,
                ParticipationRequestStatus.CONFIRMED);
        RequestStatus newStatus = request.getStatus();

        if (RequestStatus.CONFIRMED.equals(newStatus) && confirmedRequestsCount == participantLimit) {
            throw new ConflictException("Достигнут лимит по заявкам на данное событие.");
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        List<ParticipationRequest> participationRequests = participationRequestRepository.findByIdIn(request.getRequestIds());
        boolean isLimit = false;
        for (ParticipationRequest pr : participationRequests) {
            if (!ParticipationRequestStatus.PENDING.equals(pr.getStatus())) {
                throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания.");
            }
            if (RequestStatus.REJECTED.equals(newStatus) || isLimit) {
                pr.setStatus(ParticipationRequestStatus.REJECTED);
                rejectedRequests.add(ParticipationRequestMapper.toDto(pr));
            }
            if (RequestStatus.CONFIRMED.equals(newStatus)) {
                pr.setStatus(ParticipationRequestStatus.CONFIRMED);
                confirmedRequestsCount++;
                confirmedRequests.add(ParticipationRequestMapper.toDto(pr));
                if (confirmedRequestsCount == participantLimit) {
                    isLimit = true;
                }
            }
        }
        participationRequestRepository.saveAll(participationRequests);

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);
        return result;
    }

    private void checkData(LocalDateTime dateTime) {
        if (dateTime == null) return;
        if (dateTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: "
                    + dateTime);
        }
    }

    private void validateUpdateEventUserRequest(UpdateEventUserRequest request) {
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

    private static int compareByViews(EventFullDto e1, EventFullDto e2) {
        return e2.getViews() > e1.getViews() ? 1 : -1;
    }

    private List<ViewStats> findViewsForEvents(List<Event> events) {
        List<String> urisForStatistic = events.stream()
                .map(e -> "/events/" + e.getId())
                .collect(Collectors.toList());
        ViewStatsRequest request = new ViewStatsRequest(LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.now(), urisForStatistic.toArray(new String[urisForStatistic.size()]), true);
        return statsClient.findStatistic(request);
    }

    private int findConfirmedRequestCount(int eventId) {
        return participationRequestRepository.countAllByEvent_IdAndStatus(eventId,
                ParticipationRequestStatus.CONFIRMED);
    }

    private List<ParticipationStat> findConfirmedRequestCount(List<Event> events) {
        List<Integer> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        return participationRequestRepository.findParticipationRequestStatistic(eventIds,
                ParticipationRequestStatus.CONFIRMED);
    }
}
