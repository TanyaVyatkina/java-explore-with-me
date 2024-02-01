package ru.practicum.ewm.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.ParticipationRequest;
import ru.practicum.ewm.entity.User;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersEventsServiceImpl implements UsersEventsService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final EventMapper eventMapper;

    @Override
    public List<EventShortDto> getUserEvents(int userId, PageRequest page) {
        List<Event> events = eventRepository.findByInitiator_Id(userId, page);
        return eventMapper.toShortDtoList(events);
    }

    @Override
    public EventFullDto addUserEvent(int userId, NewEventDto eventDto) {
        checkData(eventDto.getEventDate());
        User initiator = userRepository.findById(userId).get();
        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new ValidationException("Категория с id = " + eventDto.getCategory()
                        + " не найдена или недоступна."));
        Event event = eventMapper.toNewEntity(eventDto, initiator, category);
        event = eventRepository.save(event);
        return eventMapper.toFullDto(event);
    }

    @Override
    public EventFullDto getUserEvent(int userId, int eventId) {
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие id = " + eventId
                        + "пользователя id = " + userId));
        return eventMapper.toFullDto(event);
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
        return eventMapper.toFullDto(event);
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
        int  participantLimit = event.getParticipantLimit();
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
}
