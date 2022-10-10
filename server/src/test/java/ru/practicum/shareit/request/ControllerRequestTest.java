package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.requests.ItemRequestController;
import ru.practicum.shareit.item.requests.dto.ItemRequestDto;
import ru.practicum.shareit.item.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.item.requests.model.ItemRequest;
import ru.practicum.shareit.item.requests.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@Import(ItemRequestMapper.class)
public class ControllerRequestTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestMapper itemRequestMapper;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item1 = new Item();
    private final Item item2 = new Item();
    private final ItemRequest itemRequest = new ItemRequest();
    private ItemRequestDto itemRequestDto = new ItemRequestDto();

    @BeforeEach
    public void init(WebApplicationContext wac) {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        objectMapper.findAndRegisterModules();
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
        item2.setId(2);
        item2.setName("item2");
        item2.setDescription("descr item2");
        item2.setAvailable(true);
        item2.setOwner(user1.getId());
        itemRequest.setId(1);
        itemRequest.setDescription("descr about the item1");
        itemRequest.setRequestor(user2);
        itemRequest.setCreated(LocalDateTime.now().withNano(0));
        itemRequest.setItems(Set.of(item1, item2));
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1);
        itemRequestDto.setDescription("descr about the item1");
        itemRequestDto.setRequestor(user2);
        itemRequestDto.setCreated(LocalDateTime.now().withNano(0));
        itemRequestDto.setItems(Set.of(item1, item2));
    }

    @Test
    public void addRequestTest() throws Exception {
        when(itemRequestService.add(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequest);
        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestMapper.toDto(itemRequest)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor", is(itemRequestDto.getRequestor()), User.class))
                .andExpect(jsonPath("$.items", is(Matchers.notNullValue())));
    }

    @Test
    public void addRequestWithInvalidDescriptionTest() throws Exception {
        itemRequest.setDescription("");
        when(itemRequestService.add(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequest);
        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestMapper.toDto(itemRequest)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllOwnTest() throws Exception {
        when(itemRequestService.getAllOwn(Mockito.anyInt()))
                .thenReturn(List.of(itemRequest));
        mockMvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", is(hasSize(1))))
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requestor", is(itemRequestDto.getRequestor()), User.class))
                .andExpect(jsonPath("$.[0].created", is(Matchers.notNullValue())))
                .andExpect(jsonPath("$.[0].items", is(Matchers.notNullValue())));
    }

    @Test
    public void getByRequestIdTest() throws Exception {
        when(itemRequestService.getById(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(itemRequest);
        mockMvc.perform(get("/requests/{requestId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor", is(itemRequestDto.getRequestor()), User.class))
                .andExpect(jsonPath("$.created", is(Matchers.notNullValue())))
                .andExpect(jsonPath("$.items", is(Matchers.notNullValue())));
    }

    @Test
    public void getAllRequestsTest() throws Exception {
        when(itemRequestService.getAll(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemRequest));
        mockMvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", is(hasSize(1))))
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requestor", is(itemRequestDto.getRequestor()), User.class))
                .andExpect(jsonPath("$.[0].created", is(Matchers.notNullValue())))
                .andExpect(jsonPath("$.[0].items", is(Matchers.notNullValue())));
    }
}
