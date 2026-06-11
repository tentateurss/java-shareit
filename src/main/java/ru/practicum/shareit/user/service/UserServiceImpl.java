package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<UserDto> findAll() {
        log.info("Запрос всех пользователей");
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Long id) {
        log.info("Поиск пользователя с id={}", id);
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Сохранение пользователя {}", userDto);
        if (userStorage.emailExists(userDto.getEmail())) {
            throw new DuplicatedDataException("Email уже используется");
        }

        User user = UserMapper.toUser(userDto);
        User created = userStorage.create(user);
        return UserMapper.toUserDto(created);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        log.info("Обновление пользователя с id={}", id);
        User existingUser = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));

        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            if (userStorage.emailExists(userDto.getEmail())) {
                throw new DuplicatedDataException("Email уже используется");
            }
            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        User updated = userStorage.update(existingUser);
        return UserMapper.toUserDto(updated);
    }

    @Override
    public void delete(Long id) {
        userStorage.delete(id);
    }
}
