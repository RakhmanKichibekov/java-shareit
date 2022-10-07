package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(UserMapper.class)
public class ControllerTest {
    @MockBean
    private UserService userService;
    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserMapper userMapper;

    private final UserDto userDto = new UserDto();
    private final UserDto userDto2 = new UserDto();

    private User user = new User();

    @BeforeEach
    public void init(WebApplicationContext wac) {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        userDto.setName("User1");
        userDto.setEmail("u1@user.com");
        userDto.setId(1);
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setId(1);
    }

    @Test
    public void addUserTest() throws Exception {
        when(userService.add(Mockito.any(User.class)))
                .thenReturn(user);
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("User1")))
                .andExpect(jsonPath("$.email", is("u1@user.com")));

    }

    @Test
    public void addUserWithInvalidEmailTest() throws Exception {
        userDto.setEmail("u1.user.com");
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void addUserWithInvalidNameTest() throws Exception {
        userDto.setName("");
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void updateUserWithUserMapperDtoTest() throws Exception {
        userDto.setName("UpdUser");
        userDto.setEmail("upd@user.com");
        user = userMapper.toUser(userDto);
        when(userService.update(Mockito.any(User.class), Mockito.anyInt()))
                .thenReturn(user);
        mvc.perform(patch("/users/{id}", userDto.getId())
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("UpdUser")))
                .andExpect(jsonPath("$.email", is("upd@user.com")));
    }

    @Test
    public void getUserByIdTest() throws Exception {
        when(userService.getById(Mockito.anyInt()))
                .thenReturn(user);
        mvc.perform(get("/users/{id}", userDto.getId())
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("User1")))
                .andExpect(jsonPath("$.email", is("u1@user.com")));
    }

    @Test
    public void getAllUsersWithUserMapperDtoTest() throws Exception {
        userDto2.setId(2);
        userDto2.setName("User2");
        userDto2.setEmail("u2@user.com");
        User user2 = userMapper.toUser(userDto2);
        when(userService.getAll())
                .thenReturn(List.of(user, user2));
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].name", is("User1")))
                .andExpect(jsonPath("$.[0].email", is("u1@user.com")))
                .andExpect(jsonPath("$.[1].id", is(2)))
                .andExpect(jsonPath("$.[1].name", is("User2")))
                .andExpect(jsonPath("$.[1].email", is("u2@user.com")));
    }

    @Test
    public void deleteUserByIdTest() throws Exception {
        mvc.perform(delete("/users/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito
                .verify(userService, Mockito.times(1))
                .delete(1);
    }

}
