package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void create_ShouldReturnUserWithId() {
        UserDto dto = new UserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@mail.ru");

        UserDto created = userService.create(dto);

        assertNotNull(created.getId());
        assertEquals("Иван", created.getName());
        assertEquals("ivan@mail.ru", created.getEmail());
    }

    @Test
    void create_ShouldThrowWhenEmailExists() {
        UserDto dto = new UserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@mail.ru");
        userService.create(dto);

        UserDto duplicate = new UserDto();
        duplicate.setName("Другой");
        duplicate.setEmail("ivan@mail.ru");

        assertThrows(RuntimeException.class, () -> userService.create(duplicate));
    }

    @Test
    void findById_ShouldReturnUser() {
        UserDto dto = new UserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@mail.ru");
        UserDto created = userService.create(dto);

        UserDto found = userService.findById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals("Иван", found.getName());
    }

    @Test
    void findById_ShouldThrowWhenNotFound() {
        assertThrows(NotFoundException.class, () -> userService.findById(999L));
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
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
    void update_ShouldUpdateOnlyName() {
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
    void delete_ShouldRemoveUser() {
        UserDto dto = new UserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@mail.ru");
        UserDto created = userService.create(dto);

        userService.delete(created.getId());

        assertThrows(NotFoundException.class, () -> userService.findById(created.getId()));
    }
}