package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.requests.model.ItemRequest;
import ru.practicum.shareit.item.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestRepositoryTest {

    private final ItemRequestRepository itemRequestRepository;
    private final TestEntityManager entityManager;
    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item1 = new Item();
    private final Item item2 = new Item();
    private final ItemRequest request1 = new ItemRequest();
    private final ItemRequest request2 = new ItemRequest();

    @BeforeEach
    public void init() {
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        user2.setId(2);
        user2.setName("user2");
        user2.setEmail("u2@user.com");
        item1.setId(1);
        item1.setName("item1");
        item1.setDescription("descr item1");
        item1.setAvailable(true);
        item1.setOwner(user1.getId());
        item1.setRequestId(1);
        item2.setId(2);
        item2.setName("item2");
        item2.setDescription("descr item2");
        item2.setAvailable(true);
        item2.setOwner(user1.getId());
        item2.setRequestId(1);
        request1.setDescription("descr about the item1");
        request1.setRequestor(user1);
        request1.setCreated(LocalDateTime.now().withNano(0));
        request2.setDescription("descr about the item2");
        request2.setRequestor(user1);
        request2.setCreated(LocalDateTime.now().withNano(0));
        entityManager.persist(user1);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.flush();
    }

    @Test
    public void addRequestTest() {
        Optional<ItemRequest> itemRequestResult = Optional.of(itemRequestRepository.save(request2));
        Assertions.assertEquals(itemRequestResult, Optional.of(request2));
    }

    @Test
    public void getAllOwnRequestsTest() {
        Collection<ItemRequest> itemRequests = itemRequestRepository.getRequestsByRequestor(1);
        Assertions.assertEquals(itemRequests.size(), 2);
        Assertions.assertEquals(itemRequests.toArray()[0], request1);
        Assertions.assertEquals(itemRequests.toArray()[1], request2);
    }

    @Test
    public void getAllRequestsTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Collection<ItemRequest> itemRequests = itemRequestRepository.getAll(2, pageable).getContent();
        Assertions.assertEquals(itemRequests.size(), 2);
        Assertions.assertEquals(itemRequests.toArray()[0], request1);
        Assertions.assertEquals(itemRequests.toArray()[1], request2);
    }
}
