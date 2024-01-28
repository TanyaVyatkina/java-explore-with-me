package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.service.admin.AdminUsersService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class AdminUsersController {
    private final AdminUsersService adminUsersService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam int[] ids,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size) {
        log.debug("Пришел запрос на получение информации о пользователях.");
        PageRequest page = PageRequest.of(from / size, size);
        List<UserDto> foundUsers = adminUsersService.getUsers(ids, page);
        log.debug("Найдены пользователи: {}.", foundUsers);
        return foundUsers;
    }

    @PostMapping
    public UserDto saveUser(@RequestBody @Valid NewUserRequest request) {
        log.debug("Пришел запрос на добавление нового пользователя.");
        UserDto savedCategory = adminUsersService.saveUser(request);
        log.debug("Пользователь сохранен.");
        return savedCategory;
    }

    @DeleteMapping("/{userId")
    public void deleteUser(@PathVariable("userId") int userId) {
        log.debug("Удаление пользователя: {}.", userId);
        adminUsersService.deleteUser(userId);
        log.debug("Пользователь удален.");
    }
}
