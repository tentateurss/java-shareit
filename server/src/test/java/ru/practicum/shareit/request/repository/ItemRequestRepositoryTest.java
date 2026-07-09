package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Test
    void findFromOthersShouldReturnRequestsFromOtherUsers() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@mail.com");

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@mail.com");

        em.persist(user1);
        em.persist(user2);

        ItemRequest requestBy1 = new ItemRequest();
        requestBy1.setDescription("Нужна дрель");
        requestBy1.setRequesterId(user1.getId());
        requestBy1.setCreated(LocalDateTime.now());
        em.persist(requestBy1);

        ItemRequest requestBy2 = new ItemRequest();
        requestBy2.setDescription("Нужна лестница");
        requestBy2.setRequesterId(user2.getId());
        requestBy2.setCreated(LocalDateTime.now());
        em.persist(requestBy2);

        List<ItemRequest> result = requestRepository.findFromOthers(user1.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Нужна лестница");
        assertThat(result.get(0).getRequesterId()).isEqualTo(user2.getId());
    }
}