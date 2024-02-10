package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(int[] ids, PageRequest page) {
        List<User> users;
        if (ids == null || ids.length == 0) {
            users = userRepository.findAll(page).getContent();
        } else {
            users = userRepository.findByIdIn(ids, page);
        }
        return UserMapper.toDtoList(users);
    }

    @Override
    @Transactional
    public UserDto saveUser(NewUserRequest request) {
        User user = userRepository.save(UserMapper.toEntity(request));
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));
        userRepository.deleteById(userId);
    }
}
