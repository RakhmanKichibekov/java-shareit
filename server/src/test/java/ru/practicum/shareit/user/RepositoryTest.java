package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    private User user1 = new User();
    private User user2 = new User();

    @Test
    public void getUserByEmailTest() {
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        entityManager.persist(user1);

        TypedQuery<User> query = entityManager.getEntityManager()
                .createQuery("select u from User u where u.email = :email", User.class);
        user2 = query.setParameter("email", user1.getEmail()).getSingleResult();
        Assertions.assertEquals(user1.getName(), user2.getName());
        Assertions.assertEquals(user1.getEmail(), user2.getEmail());
    }
}
