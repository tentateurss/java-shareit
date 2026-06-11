package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

class ItemServiceTest {

    private ItemService itemService;
    private ItemStorage itemStorage;
    private UserStorage userStorage;
    private Long ownerId;

    @BeforeEach
    void setUp() {
        itemStorage = new InMemoryItemStorage();
        userStorage = new InMemoryUserStorage();
        itemService = new ItemServiceImpl(itemStorage, userStorage);

        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@mail.ru");
        User created = userStorage.create(owner);
        ownerId = created.getId();
    }

    @Test
    void createShouldReturnItemWithId() {
        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная");
        dto.setAvailable(true);

        ItemDto created = itemService.create(ownerId, dto);

        assertNotNull(created.getId());
        assertEquals("Дрель", created.getName());
        assertEquals(ownerId, created.getOwnerId());
    }

    @Test
    void createShouldThrowWhenUserNotFound() {
        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная");
        dto.setAvailable(true);

        assertThrows(NotFoundException.class, () -> itemService.create(999L, dto));
    }

    @Test
    void findByIdShouldReturnItem() {
        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная");
        dto.setAvailable(true);
        ItemDto created = itemService.create(ownerId, dto);

        ItemDto found = itemService.findById(created.getId());

        assertEquals("Дрель", found.getName());
    }

    @Test
    void findAllByOwnerIdShouldReturnOwnersItems() {
        itemService.create(ownerId, createItemDto("Дрель", "Описание", true));
        itemService.create(ownerId, createItemDto("Молоток", "Описание", true));

        List<ItemDto> items = itemService.findAllByOwnerId(ownerId);

        assertEquals(2, items.size());
    }

    @Test
    void searchShouldFindByName() {
        itemService.create(ownerId, createItemDto("Дрель", "Мощная", true));
        itemService.create(ownerId, createItemDto("Молоток", "Тяжёлый", false));

        List<ItemDto> result = itemService.search("дрель");

        assertEquals(1, result.size());
        assertEquals("Дрель", result.get(0).getName());
    }

    @Test
    void searchShouldNotFindUnavailable() {
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
