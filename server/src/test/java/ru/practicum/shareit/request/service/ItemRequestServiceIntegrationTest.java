package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestServiceImpl requestService; // Тестируемый сервис

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByRequesterShouldReturnRequestsWithItems() {
        User requester = new User();
        requester.setName("Иван");
        requester.setEmail("ivan@test.com");
        requester = userRepository.save(requester);

        User owner = new User();
        owner.setName("Пётр");
        owner.setEmail("petr@test.com");
        owner = userRepository.save(owner);

        ItemRequest request = new ItemRequest();
        request.setDescription("Ищу дрель для ремонта");
        request.setRequesterId(requester.getId());
        request.setCreated(LocalDateTime.now());
        request = requestRepository.save(request);

        Item item = new Item();
        item.setName("Ударная дрель");
        item.setDescription("Мощная, 800 Вт");
        item.setAvailable(true);
        item.setOwnerId(owner.getId());
        item.setRequestId(request.getId());
        itemRepository.save(item);

        List<ItemRequestDto> result = requestService.findByRequester(requester.getId());

        assertThat(result).isNotNull().isNotEmpty();
    }
}