package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Map;

public interface UserRepository {

    Map<Integer, User> getRepository();

    List<User> findAll();

    User findById(Integer id);

    User add(User user);

    User change(User user);

    User deleteById(Integer id);
}
