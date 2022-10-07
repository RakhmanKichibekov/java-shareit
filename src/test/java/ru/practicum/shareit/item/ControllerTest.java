package ru.practicum.shareit.item;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@Import({ItemMapper.class, CommentMapper.class})
public class ControllerTest {
    @MockBean
    private ItemService itemService;

    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private CommentMapper commentMapper;

    private final User user1 = new User();
    private final ItemDto itemDto1 = new ItemDto();
    private final ItemDto itemDto2 = new ItemDto();
    private final ItemDtoDate itemDtoDate1 = new ItemDtoDate();
    private final ItemDtoDate itemDtoDate2 = new ItemDtoDate();
    private Item item = new Item();

    @BeforeEach
    public void init(WebApplicationContext wac) {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("u1@user.com");

        itemDto1.setName("item1");
        itemDto1.setDescription("descr of item1");
        itemDto1.setAvailable(true);
        itemDto1.setOwner(1);

        itemDto2.setName("item2");
        itemDto2.setDescription("descr of item2");
        itemDto2.setAvailable(true);
        itemDto2.setOwner(1);

        itemDtoDate1.setName("item1");
        itemDtoDate1.setDescription("descr of item1");
        itemDtoDate1.setAvailable(true);
        itemDtoDate1.setOwner(1);

        itemDtoDate2.setName("item2");
        itemDtoDate2.setDescription("descr of item2");
        itemDtoDate2.setAvailable(true);
        itemDtoDate2.setOwner(1);


    }

    @Test
    public void addItemTest() throws Exception {
        item = itemMapper.toItem(itemDto1, 1, null);
        when(itemService.add(Mockito.any(Item.class)))
                .thenReturn(item);
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.owner", is(item.getOwner())));
    }

    @Test
    public void updateItemTest() throws Exception {
        itemDto1.setId(1);
        itemDto1.setName("updItem");
        itemDto1.setDescription("descr of updItem");
        item = itemMapper.toItem(itemDto1, user1.getId(), itemDto1.getId());
        when(itemService.update(Mockito.any(Item.class), Mockito.anyInt()))
                .thenReturn(item);
        mvc.perform(patch("/items/{id}", itemDto1.getId())
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.owner", is(item.getOwner())));
    }

    @Test
    public void getItemByIdTest() throws Exception {
        when(itemService.getItemDate(Mockito.anyInt(), Mockito.any(LocalDateTime.class), Mockito.anyInt()))
                .thenReturn(itemDtoDate1);
        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDtoDate1)));
    }

    @Test
    public void getAllItemsByOwnerTest() throws Exception {
        when(itemService.getAll(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemDtoDate1, itemDtoDate2));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDtoDate1, itemDtoDate2))));
    }

    @Test
    public void getByNameOrDescItemTest() throws Exception {
        item = itemMapper.toItem(itemDto1, 1, null);
        Item item2 = itemMapper.toItem(itemDto2, 1, null);
        when(itemService.getByNameOrDesc(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(item, item2));
        String strSearch = "DESCR";
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", strSearch)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(item, item2))));
    }

    @Test
    public void addCommentTest() throws Exception {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setAuthor(1);
        comment.setItem(1);
        comment.setText("comment about item1");
        CommentDto commentDto = commentMapper.toDto(comment, user1);
        when(itemService.addComment(Mockito.any(CommentDto.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(commentDto);
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId())))
                .andExpect(jsonPath("$.author", is(comment.getAuthor())))
                .andExpect(jsonPath("$.item", is(comment.getItem())))
                .andExpect(jsonPath("$.text", is(comment.getText())));
    }
}