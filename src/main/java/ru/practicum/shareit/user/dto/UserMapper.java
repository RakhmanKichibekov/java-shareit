package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class UserMapper {

    public Collection<UserDto> getAll(UserService userServiceImpl, UserMapper userMapper) {
        ArrayList<UserDto> list = new ArrayList<>();
        for (User user : userServiceImpl.getAll()) {
            list.add(userMapper.toDto(user));
        }
        return list;
    }

    public User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public void deleteAll(UserService userServiceImpl) {
        userServiceImpl.deleteAll();
    }
}
