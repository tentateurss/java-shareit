package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void testUserCrudFlow() {
        UserDto dto = new UserDto();
        dto.setName("Integration User");
        dto.setEmail("integration@test.com");

        // 1 create
        UserDto created = userService.create(dto);
        assertThat(created.getId()).isNotNull();

        // 2 find FindById
        UserDto found = userService.findById(created.getId());
        assertThat(found.getName()).isEqualTo("Integration User");

        // 3 update
        found.setName("Updated Integration");
        UserDto updated = userService.update(created.getId(), found);
        assertThat(updated.getName()).isEqualTo("Updated Integration");

        // 4 find All
        List<UserDto> all = userService.findAll();
        assertThat(all).isNotEmpty();

        // 5 delete
        userService.delete(created.getId());
    }
}