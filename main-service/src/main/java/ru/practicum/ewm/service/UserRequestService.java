package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.ParticipationRequestDto;

import java.util.List;

public interface UserRequestService {
    List<ParticipationRequestDto> getUserRequests(int userId);

    ParticipationRequestDto addUserRequest(int userId, int requestId);

    ParticipationRequestDto cancelUserRequest(int userId, int requestId);

}
