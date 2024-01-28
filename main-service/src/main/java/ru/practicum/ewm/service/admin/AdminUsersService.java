package ru.practicum.ewm.service.admin;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserShortDto;

import javax.validation.Valid;
import java.util.List;

public interface AdminUsersService {
    List<UserDto> getUsers(int[] ids, PageRequest page);
    UserDto saveUser(NewUserRequest request);
    void deleteUser(int userId);
}
