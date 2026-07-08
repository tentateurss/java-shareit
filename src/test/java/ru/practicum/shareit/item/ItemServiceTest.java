package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private Long ownerId;

    @BeforeEach
    void setUp() {
        UserDto owner = new UserDto();
        owner.setName("Владелец");
        owner.setEmail("owner@mail.ru");
        UserDto created = userService.create(owner);
        ownerId = created.getId();
    }

    @Test
    void create_ShouldReturnItemWithId() {
        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная");
        dto.setAvailable(true);

        ItemDto created = itemService.create(ownerId, dto);

        assertNotNull(created.getId());
        assertEquals("Дрель", created.getName());
    }

    @Test
    void create_ShouldThrowWhenUserNotFound() {
        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная");
        dto.setAvailable(true);

        assertThrows(NotFoundException.class, () -> itemService.create(999L, dto));
    }

    @Test
    void findById_ShouldReturnItem() {
        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная");
        dto.setAvailable(true);
        ItemDto created = itemService.create(ownerId, dto);

        ItemDto found = itemService.findById(created.getId(), ownerId);

        assertEquals("Дрель", found.getName());
    }

    @Test
    void findAllByOwnerId_ShouldReturnOwnersItems() {
        itemService.create(ownerId, createItemDto("Дрель", "Описание", true));
        itemService.create(ownerId, createItemDto("Молоток", "Описание", true));

        List<ItemDto> items = itemService.findAllByOwnerId(ownerId);

        assertEquals(2, items.size());
    }

    @Test
    void search_ShouldFindByName() {
        itemService.create(ownerId, createItemDto("Дрель", "Мощная", true));
        itemService.create(ownerId, createItemDto("Молоток", "Тяжёлый", false));

        List<ItemDto> result = itemService.search("дрель");

        assertEquals(1, result.size());
        assertEquals("Дрель", result.get(0).getName());
    }

    @Test
    void search_ShouldNotFindUnavailable() {
        itemService.create(ownerId, createItemDto("Дрель", "Мощная", false));

        List<ItemDto> result = itemService.search("дрель");

        assertTrue(result.isEmpty());
    }

    private ItemDto createItemDto(String name, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }
}