package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.exception.UserValidationException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAll() {
        List<UserDto> list = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            list.add(UserMapper.toUserDto(user));
        }
        return list;
    }

    @Override
    public UserDto findById(Integer id) {
        if (!userRepository.getRepository().containsKey(id)) {
            log.warn("Пользователь с идентификатором {} не найден.", id);
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }
        return UserMapper.toUserDto(userRepository.findById(id));
    }

    @Override
    public UserDto add(UserDto userDto) {
        validateEmail(userDto);
        return UserMapper.toUserDto(userRepository.add(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto change(Integer id, UserDto userDto) {
        if (!userRepository.getRepository().containsKey(id)) {
            log.warn("Пользователь с идентификатором {} не найден.", id);
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }
        validateEmail(userDto);
        userDto.setId(id);
        User oldUser = userRepository.getRepository().get(id);
        if (userDto.getName() == null) {
            userDto.setName(oldUser.getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(oldUser.getEmail());
        }
        return UserMapper.toUserDto(userRepository.change(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto deleteById(Integer id) {
        if (!userRepository.getRepository().containsKey(id)) {
            log.warn("Пользователь с идентификатором {} не найден.", id);
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }
        return UserMapper.toUserDto(userRepository.deleteById(id));
    }

    private void validateEmail(UserDto userDto) {
        for (User u : userRepository.getRepository().values()) {
            if (u.getEmail().equals(userDto.getEmail()) && !u.getId().equals(userDto.getId())) {
                log.warn("Дубликат email");
                throw new UserValidationException("Пользователь с " + userDto.getEmail() + " уже существует");
            }
        }
    }
}
