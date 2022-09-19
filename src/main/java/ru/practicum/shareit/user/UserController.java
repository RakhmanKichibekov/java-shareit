package ru.practicum.shareit.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.ConflictException;
import ru.practicum.shareit.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value ="/",produces = "application/json")
    public String getURLValue(HttpServletRequest request){
        String test = request.getRequestURI();
        return test;
    }
    @GetMapping()
    protected List<UserDto> findAll() {
        return userService.findAllUser();
    }

    @GetMapping("/{id}")
    protected UserDto findUserById(@PathVariable Optional<Long> id) {
        return userService.findUserById(id);
    }

    @PatchMapping("/{id}")
    protected UserDto put(@PathVariable Optional<Long> id, @RequestBody UserDto userDto)
            throws ValidationException, ConflictException {
        return userService.patchUser(userDto, id);
    }
    @DeleteMapping("/{id}")
    protected UserDto deleteUser(@PathVariable Optional<Long> id) {
        return userService.deleteUser(id);
    }

    @PostMapping()
    protected UserDto create(@Valid @RequestBody UserDto userDto) throws ValidationException, ConflictException {
        return userService.createUser(userDto);
    }
}
