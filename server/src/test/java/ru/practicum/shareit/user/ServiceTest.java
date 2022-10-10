package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final User user1 = new User();
    private final User user2 = new User();

    @BeforeEach
    public void init() {
        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        user2.setId(2);
        user2.setName("user2");
        user2.setEmail("u2@user.com");
    }

    @Test
    public void addUserTest() {
        Mockito
                .when(userRepository.save(user1))
                .thenReturn(user1);
        User newUser = userService.add(user1);
        Assertions.assertEquals(user1, newUser);
    }

    @Test
    public void updateUserTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user1));
        user2.setId(1);
        user2.setName("UpdUser");
        user2.setEmail("upd@user.com");
        Mockito
                .when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user2);
        User updUser = new User();
        updUser.setName("UpdUser");
        updUser.setEmail("upd@user.com");
        User resultUser = userService.update(updUser, 1);
        Assertions.assertEquals(resultUser.getId(), 1);
        Assertions.assertEquals(resultUser.getName(), updUser.getName());
        Assertions.assertEquals(resultUser.getEmail(), updUser.getEmail());
    }

    @Test
    public void getByUserIdTest() {
        Mockito
                .when(userRepository.findById(1))
                .thenReturn(Optional.of(user1));
        User resultUser = userService.getById(1);
        Assertions.assertEquals(resultUser.getId(), 1);
        Assertions.assertEquals(resultUser.getName(), user1.getName());
        Assertions.assertEquals(resultUser.getEmail(), user1.getEmail());
    }

    @Test
    public void getAllUsersTest() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));
        Collection<User> usersResult = userService.getAll();
        Assertions.assertEquals(usersResult.size(), 2);
        Assertions.assertEquals(usersResult, List.of(user1, user2));
    }

    @Test
    public void deleteUserByIdTest() {
        userService.delete(1);
        Mockito
                .verify(userRepository, Mockito.times(1))
                .deleteById(1);
    }

    @Test
    public void deleteAllUsersTest() {
        userService.deleteAll();
        Mockito
                .verify(userRepository, Mockito.times(1))
                .deleteAll();
    }

    @Test
    public void getUserWithInvalidId() {
        Mockito
                .when(userRepository.findById(99))
                .thenReturn(null);
        Assertions.assertThrows(NullPointerException.class, () -> userService.getById(99));
    }
}
