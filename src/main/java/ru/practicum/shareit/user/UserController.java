package ru.practicum.shareit.user;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userServiceImpl;

    private final UserMapper userMapper;

    public UserController(UserService userServiceImpl, UserMapper userMapper) {
        this.userServiceImpl = userServiceImpl;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Integer id) {
        return userMapper.toDto(userServiceImpl.getById(id));
    }

    @GetMapping
    public Collection<UserDto> getAll(){
        return userMapper.getAll(userServiceImpl, userMapper);
    }

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toDto(userServiceImpl.add(user));
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Integer id) {
        User user = userMapper.toUser(userDto);
        return userMapper.toDto(userServiceImpl.update(user, id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        userServiceImpl.delete(id);
    }

    @DeleteMapping
    public void deleteAll(UserService userServiceImpl) {
        userMapper.deleteAll(userServiceImpl);
    }

}
