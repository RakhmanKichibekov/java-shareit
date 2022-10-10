package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.requests.model.ItemRequest;
import ru.practicum.shareit.item.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.item.requests.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class ServiceRequestUnitTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item1 = new Item();
    private final Item item2 = new Item();
    private final ItemRequest itemRequest = new ItemRequest();

    @BeforeEach
    public void init() {
        user1.setId(1);
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
        itemRequest.setId(1);
        itemRequest.setDescription("descr about the item1");
        itemRequest.setRequestor(user2);
        itemRequest.setCreated(LocalDateTime.now().withNano(0));
        itemRequest.setItems(Set.of(item1, item2));

    }

    @Test
    public void addRequestTest() {
        Mockito
                .when(itemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequest);
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user2));
        ItemRequest itemRequestResult = itemRequestService.add(itemRequest);
        Assertions.assertEquals(itemRequestResult, itemRequest);
    }

    @Test
    public void addRequestTestWithInvalidRequestorId() {
        user2.setId(99);
        Mockito
                .when(userRepository.findById(99))
                .thenThrow(ResponseStatusException.class);
        Assertions.assertThrows(ResponseStatusException.class, () -> itemRequestService.add(itemRequest));
    }

    @Test
    public void getAllOwnRequestsTest() {
        Mockito
                .when(itemRequestRepository.getRequestsByRequestor(Mockito.anyInt()))
                .thenReturn(List.of(itemRequest));
        Collection<ItemRequest> list = itemRequestRepository.getRequestsByRequestor(2);
        Assertions.assertEquals(list.size(), 1);
    }

    @Test
    public void getByIdRequestTest() {
        Mockito
                .when(itemRequestRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(itemRequest));
        Optional<ItemRequest> itemRequestResult = (Optional<ItemRequest>) itemRequestRepository.findById(1);
        Assertions.assertEquals(itemRequestResult, Optional.of(itemRequest));
    }

    @Test
    public void getAllRequestsTest() {
        List<ItemRequest> list = List.of(itemRequest);
        Pageable pageable = PageRequest.of(0, 1);
        Page<ItemRequest> page = new PageImpl<>(list, pageable, list.size());
        Mockito
                .when(itemRequestRepository.getAll(Mockito.anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(page);
        Collection<ItemRequest> list1 =
                itemRequestRepository.getAll(2, PageRequest.of(0, 10)).getContent();
        Assertions.assertEquals(list1.size(), 1);
    }
}