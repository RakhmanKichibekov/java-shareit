package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

public class UserDtoMappingTest {
    private final UserMapper userMapper = new UserMapper();
    private final User user = new User();
    private final UserDto dto = new UserDto();

    @BeforeEach
    public void init() {
        user.setId(1);
        user.setName("user");
        user.setEmail("u@user.com");

        dto.setId(1);
        dto.setName("user");
        dto.setEmail("u@user.com");
    }

    @Test
    public void toDtoFromUserTest() {
        UserDto result = userMapper.toDto(user);
        Assertions.assertEquals(result, dto);
    }

    @Test
    public void toUserFromDtoTest() {
        User result = userMapper.toUser(dto);
        Assertions.assertEquals(result, user);
    }
}
