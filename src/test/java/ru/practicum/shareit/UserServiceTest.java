package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {

    private UserService userService;
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserServiceImpl(userStorage);
    }

    @Test
    void createShouldReturnUserWithId() {
        UserDto dto = new UserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@mail.ru");

        UserDto created = userService.create(dto);

        assertNotNull(created.getId());
        assertEquals("Иван", created.getName());
        assertEquals("ivan@mail.ru", created.getEmail());
    }

    @Test
    void createShouldThrowWhenEmailExists() {
        UserDto dto = new UserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@mail.ru");
        userService.create(dto);

        UserDto duplicate = new UserDto();
        duplicate.setName("Другой");
        duplicate.setEmail("ivan@mail.ru");

        assertThrows(DuplicatedDataException.class, () -> userService.create(duplicate));
    }

    @Test
    void findByIdShouldReturnUser() {
        UserDto dto = new UserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@mail.ru");
        UserDto created = userService.create(dto);

        UserDto found = userService.findById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals("Иван", found.getName());
    }

    @Test
    void findByIdShouldThrowWhenNotFound() {
        assertThrows(NotFoundException.class, () -> userService.findById(999L));
    }

    @Test
    void findAllShouldReturnAllUsers() {
        UserDto dto1 = new UserDto();
        dto1.setName("Иван");
        dto1.setEmail("ivan@mail.ru");
        userService.create(dto1);

        UserDto dto2 = new UserDto();
        dto2.setName("Пётр");
        dto2.setEmail("petr@mail.ru");
        userService.create(dto2);

        List<UserDto> users = userService.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void updateShouldUpdateOnlyName() {
        UserDto dto = new UserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@mail.ru");
        UserDto created = userService.create(dto);

        UserDto update = new UserDto();
        update.setName("Пётр");

        UserDto updated = userService.update(created.getId(), update);

        assertEquals("Пётр", updated.getName());
        assertEquals("ivan@mail.ru", updated.getEmail());
    }

    @Test
    void deleteShouldRemoveUser() {
        UserDto dto = new UserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@mail.ru");
        UserDto created = userService.create(dto);

        userService.delete(created.getId());

        assertThrows(NotFoundException.class, () -> userService.findById(created.getId()));
    }

    @Test
    void updateShouldThrowWhenEmailAlreadyExists() {
        UserDto user1 = new UserDto();
        user1.setName("Иван");
        user1.setEmail("ivan@mail.ru");
        UserDto created1 = userService.create(user1);

        UserDto user2 = new UserDto();
        user2.setName("Пётр");
        user2.setEmail("petr@mail.ru");
        userService.create(user2);

        UserDto updateDto = new UserDto();
        updateDto.setEmail("petr@mail.ru");

        assertThrows(DuplicatedDataException.class, () ->
                userService.update(created1.getId(), updateDto)
        );
    }
}
