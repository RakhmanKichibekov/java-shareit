package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.ConflictException;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> findAllUser() {
        List<User> list = userRepository.findAll();
        log.info("Текущее количество пользователей в списке: {}", list.size());
        return UserMapper.toListUserDto(list);
    }

    public UserDto findUserById(Optional<Long> id) {
        if (id.isPresent()) {
            User user = userRepository.findById(id.get()).get();
            log.info("Пользователь по id запросу: {}", user.getName());
            return UserMapper.toUserDto(user);
        }
        throw new NoSuchElementException("Не правильно задан id пользователя! findUserById()");
    }

    public UserDto deleteUser(Optional<Long> id){
        if (id.isPresent()) {
            User user = userRepository.findById(id.get()).get();
            userRepository.delete(user);
            log.info("Пользователь: {} удален.", user);
            return UserMapper.toUserDto(user);
        }
        throw new NoSuchElementException("Переменные пути указаны не верно! deleteUser()");
    }

    public UserDto createUser(UserDto userDto) throws ValidationException, ConflictException {
        User user = UserMapper.toUser(userDto);
        if (validationUser(user)) {
            userRepository.save(user);
            log.info("Добавлен пользователь: {}", user.getName());
            return UserMapper.toUserDto(user);
        }
        throw new ValidationException("Пользователь на создан! createUser()");
    }

    public UserDto patchUser(UserDto userDto, Optional<Long> id) throws ValidationException, ConflictException {
        User user = UserMapper.toUser(userDto);
        if (!id.isPresent())
            throw new NoSuchElementException("Отсутствует id пользователя! patchUser()");
        user.setId(id.get());
        if (user.getName() == null)
            user.setName(userRepository.findById(id.get()).get().getName());
        if (user.getEmail() == null)
            user.setEmail(userRepository.findById(id.get()).get().getEmail());
        userRepository.save(user);
        log.info("Данные пользователя: {} изменены.", user.getName());
        return UserMapper.toUserDto(user);
    }

    public boolean validationUser(User user) throws ValidationException, ConflictException {
        if (user.getName() != null && user.getName() != "") {
            for (char ch : user.getName().toCharArray()) {
                if (ch == ' ') {
                    throw new ValidationException("Имя не может содержать символ пробела! validationUser()");
                }
            }
        }
        return true;
    }
}