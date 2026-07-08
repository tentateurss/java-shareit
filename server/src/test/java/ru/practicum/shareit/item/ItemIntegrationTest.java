package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void testItemCrudFlow() {
        UserDto ownerDto = new UserDto();
        ownerDto.setName("Owner");
        ownerDto.setEmail("owner-int@test.com");
        UserDto owner = userService.create(ownerDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Строительная");
        itemDto.setAvailable(true);

        // create
        ItemDto created = itemService.create(owner.getId(), itemDto);
        assertThat(created.getId()).isNotNull();

        // find
        ItemDto found = itemService.findById(created.getId(), owner.getId());
        assertThat(found.getName()).isEqualTo("Дрель");

        // update
        found.setName("Супер Дрель");
        ItemDto updated = itemService.update(owner.getId(), created.getId(), found);
        assertThat(updated.getName()).isEqualTo("Супер Дрель");

        // search
        var searchResult = itemService.search("Супер");
        assertThat(searchResult).isNotEmpty();
    }
}