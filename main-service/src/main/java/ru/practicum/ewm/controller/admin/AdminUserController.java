package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) int[] ids,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size) {
        log.debug("Пришел запрос на получение информации о пользователях.");
        PageRequest page = PageRequest.of(from / size, size);
        List<UserDto> foundUsers = userService.getUsers(ids, page);
        log.debug("Найдены пользователи: {}.", foundUsers);
        return foundUsers;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveUser(@RequestBody @Valid NewUserRequest request) {
        log.debug("Пришел запрос на добавление нового пользователя.");
        UserDto savedUser = userService.saveUser(request);
        log.debug("Пользователь сохранен.");
        return savedUser;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") int userId) {
        log.debug("Удаление пользователя: {}.", userId);
        userService.deleteUser(userId);
        log.debug("Пользователь удален.");
    }
}
