package ru.practicum.ewm.service.admin;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;

import java.util.List;

public interface AdminUsersService {
    List<UserDto> getUsers(int[] ids, PageRequest page);

    UserDto saveUser(NewUserRequest request);

    void deleteUser(int userId);
}
