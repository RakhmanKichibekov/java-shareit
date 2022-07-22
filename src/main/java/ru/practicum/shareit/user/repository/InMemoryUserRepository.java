package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.IdGenerator;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserRepository implements UserRepository {

    private final Map<Integer, User> repository = new HashMap<>();
    private final IdGenerator idGenerator = new IdGenerator();


    @Override
    public Map<Integer, User> getRepository() {
        return repository;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public User findById(Integer id) {
        return repository.get(id);
    }

    @Override
    public User add(User user) {
        user.setId(idGenerator.getNextIdCount());
        repository.put(user.getId(), user);
        return user;
    }

    @Override
    public User change(User user) {
        repository.put(user.getId(), user);
        return user;
    }

    @Override
    public User deleteById(Integer id) {
        return repository.remove(id);
    }
}
