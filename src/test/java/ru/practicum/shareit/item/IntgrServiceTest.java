package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoDate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntgrServiceTest {
    private final EntityManager entityManager;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item1 = new Item();
    private final BookingDto booking1 = new BookingDto();
    private final BookingDto booking2 = new BookingDto();
    private final CommentDto commentDto = new CommentDto();

    @BeforeEach
    public void init() {
        entityManager.createNativeQuery("set referential_integrity false;").executeUpdate();
        entityManager.createNativeQuery("truncate table items restart identity;").executeUpdate();
        entityManager.createNativeQuery("truncate table users restart identity;").executeUpdate();
        entityManager.createNativeQuery("truncate table bookings restart identity;").executeUpdate();
        entityManager.createNativeQuery("truncate table requests restart identity;").executeUpdate();
        entityManager.createNativeQuery("truncate table comments restart identity;").executeUpdate();
        entityManager.createNativeQuery("set referential_integrity true;").executeUpdate();

        item1.setId(1);
        item1.setAvailable(true);
        item1.setName("item1");
        item1.setDescription("descr of item1");
        item1.setOwner(1);

        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        user2.setId(2);
        user2.setName("user2");
        user2.setEmail("u2@user.com");

        booking1.setId(1);
        booking1.setStart(LocalDateTime.now().plusDays(1).withNano(0));
        booking1.setEnd(LocalDateTime.now().plusDays(2).withNano(0));
        booking1.setItemId(1);
        booking1.setUserId(2);
        booking1.setStatus(Status.APPROVED);

        booking2.setId(2);
        booking2.setStart(LocalDateTime.now().plusDays(3).withNano(0));
        booking2.setEnd(LocalDateTime.now().plusDays(4).withNano(0));
        booking2.setItemId(1);
        booking2.setUserId(2);
        booking2.setStatus(Status.APPROVED);

        commentDto.setAuthor(2);
        commentDto.setItem(1);
        commentDto.setText("comment about item1");
    }

    @Test
    public void addItemTest() {
        userService.add(user1);
        itemService.add(item1);
        TypedQuery<Item> query = entityManager.createQuery("select i from Item i where i.name = :name", Item.class);
        Item itemResult = query.setParameter("name", item1.getName()).getSingleResult();
        Assertions.assertEquals(itemResult.getId(), item1.getId());
        Assertions.assertEquals(itemResult.getName(), item1.getName());
        Assertions.assertEquals(itemResult.getDescription(), item1.getDescription());
        Assertions.assertEquals(itemResult.getAvailable(), item1.getAvailable());
        Assertions.assertEquals(itemResult.getOwner(), item1.getOwner());
    }

    @Test
    public void addItemWithAbsencedUserTest() {
        Assertions.assertThrows(ResponseStatusException.class, () -> itemService.add(item1));
    }

    @Test
    public void addItemWithInvalidFieldsTest() {
        userService.add(user1);
        item1.setAvailable(null);
        Assertions.assertThrows(ResponseStatusException.class, () -> itemService.add(item1));
    }

    @Test
    public void updateItemTest() {
        userService.add(user1);
        itemService.add(item1);
        Item item = new Item();
        item.setId(1);
        item.setName("updName");
        item.setDescription("updDesc");
        item.setOwner(1);
        item.setAvailable(true);
        itemService.update(item, user1.getId());
        TypedQuery<Item> query = entityManager.createQuery("select i from Item i where i.name = :name", Item.class);
        Item itemResult = query.setParameter("name", item.getName()).getSingleResult();
        Assertions.assertEquals(itemResult.getId(), item.getId());
        Assertions.assertEquals(itemResult.getName(), item.getName());
        Assertions.assertEquals(itemResult.getDescription(), item.getDescription());
        Assertions.assertEquals(itemResult.getAvailable(), item.getAvailable());
        Assertions.assertEquals(itemResult.getOwner(), item.getOwner());
    }

    @Test
    public void updateItemWithInvalidOwnerTest() {
        userService.add(user1);
        userService.add(user2);
        itemService.add(item1);
        Item item = new Item();
        item.setId(1);
        item.setName("updName");
        item.setDescription("updDesc");
        item.setOwner(2);
        item.setAvailable(true);
        Assertions.assertThrows(ResponseStatusException.class, () -> itemService.update(item, user2.getId()));
    }

    @Test
    public void findByIdItemTest() {
        userService.add(user1);
        itemService.add(item1);
        Item itemResult = itemService.getById(item1.getId());
        Assertions.assertEquals(itemResult.getId(), item1.getId());
        Assertions.assertEquals(itemResult.getAvailable(), item1.getAvailable());
        Assertions.assertEquals(itemResult.getName(), item1.getName());
        Assertions.assertEquals(itemResult.getDescription(), item1.getDescription());
        Assertions.assertEquals(itemResult.getOwner(), item1.getOwner());
    }

    @Test
    public void getItemByNameOrDescriptionTest() {
        Item item2 = new Item();
        item2.setId(2);
        item2.setName("item2");
        item2.setDescription("Descr Item2");
        item2.setOwner(1);
        item2.setAvailable(true);
        userService.add(user1);
        itemService.add(item1);
        itemService.add(item2);
        Collection<Item> items = itemService.getByNameOrDesc("item", 0, 10);
        Assertions.assertEquals(items.size(), 2);
        items = itemService.getByNameOrDesc("DeSc", 0, 10);
        Assertions.assertEquals(items.size(), 2);
    }

    @Test
    public void getAllItemsByOwnerTest() {
        Item item2 = new Item();
        item2.setId(2);
        item2.setName("item2");
        item2.setDescription("Descr Item2");
        item2.setOwner(1);
        item2.setAvailable(true);
        userService.add(user1);
        itemService.add(item1);
        itemService.add(item2);
        List<ItemDtoDate> itemDtoDates = (List<ItemDtoDate>) itemService.getAll(1, 0, 10);
        Assertions.assertEquals(itemDtoDates.size(), 2);
        Assertions.assertEquals(itemDtoDates.get(0).getId(), item1.getId());
        Assertions.assertEquals(itemDtoDates.get(1).getId(), item2.getId());
        Assertions.assertEquals(itemDtoDates.get(0).getAvailable(), item1.getAvailable());
        Assertions.assertEquals(itemDtoDates.get(1).getAvailable(), item2.getAvailable());

    }

    @Test
    public void addCommentTest() {
        userService.add(user1);
        userService.add(user2);
        itemService.add(item1);
        bookingService.add(booking1, 2);
        bookingService.updBookingDate(1, LocalDateTime.now().minusDays(5));
        CommentDto commentDtoResult = itemService.addComment(commentDto, 1, 2);
        Assertions.assertEquals(commentDtoResult.getId(), 1);
    }

    @Test
    public void getItemWithDateBookingTest() {
        userService.add(user1);
        userService.add(user2);
        itemService.add(item1);
        bookingService.add(booking1, 2);
        bookingService.add(booking2, 2);
        bookingService.updBookingDate(1, LocalDateTime.now().minusDays(20));
        bookingService.updBookingDate(2, LocalDateTime.now().minusDays(10));

        itemService.addComment(commentDto, 1, 2);
        ItemDtoDate itemDtoDate = itemService.getItemDate(1, LocalDateTime.now().minusDays(15), 2);
        Assertions.assertEquals(itemDtoDate.getId(), 1);
        Assertions.assertEquals(itemDtoDate.getName(), item1.getName());
        Assertions.assertEquals(itemDtoDate.getDescription(), item1.getDescription());
        Assertions.assertEquals(itemDtoDate.getAvailable(), item1.getAvailable());
        Assertions.assertEquals(itemDtoDate.getOwner(), item1.getOwner());
    }

    @Test
    public void getUserTest() {
        userService.add(user1);
        User userResult = userService.getById(user1.getId());
        Assertions.assertEquals(userResult, user1);
    }

    @Test
    public void deleteItemTest() {
        userService.add(user1);
        itemService.add(item1);
        Assertions.assertEquals(itemService.getById(1), item1);
        itemService.delete(1, 1);
        Assertions.assertThrows(ResponseStatusException.class, () -> itemService.getById(1));
    }
}
