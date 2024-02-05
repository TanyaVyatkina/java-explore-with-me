package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EventState;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.dto.ParticipationRequestStatus;
import ru.practicum.ewm.dto.ParticipationStat;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.ParticipationRequest;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRequestServiceImpl implements UserRequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));
        List<ParticipationRequest> requests = participationRequestRepository.findByRequester_Id(userId);
        return ParticipationRequestMapper.toDtoList(requests);
    }

    @Override
    public ParticipationRequestDto addUserRequest(int userId, int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с id = " + eventId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии.");
        }
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии.");
        }
        int userRequestsCount = participationRequestRepository.countAllByRequester_IdAndEventId(userId, eventId);
        if (userRequestsCount > 0) {
            throw new ConflictException("Нельзя добавить повторный запрос.");
        }

        if (event.getParticipantLimit() != 0) {
            List<ParticipationStat> participationStats = participationRequestRepository
                    .findParticipationRequestStatistic(List.of(eventId), ParticipationRequestStatus.CONFIRMED);
            if (!participationStats.isEmpty()
                    && participationStats.get(0).getRequestCount() == event.getParticipantLimit()) {
                throw new ConflictException("У события достигнут лимит запросов на участие.");
            }
        }
        ParticipationRequest request = new ParticipationRequest();
        request.setEvent(event);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            request.setStatus(ParticipationRequestStatus.CONFIRMED);
        } else {
            request.setStatus(ParticipationRequestStatus.PENDING);
        }
        request = participationRequestRepository.save(request);
        return ParticipationRequestMapper.toDto(request);
    }

    @Override
    public ParticipationRequestDto cancelUserRequest(int userId, int requestId) {
        ParticipationRequest request = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Не найден запрос с id = " + requestId));
        request.setStatus(ParticipationRequestStatus.CANCELED);
        participationRequestRepository.save(request);
        return ParticipationRequestMapper.toDto(request);
    }
}
