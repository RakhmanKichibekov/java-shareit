package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private final Booking booking1 = new Booking();
    private final Booking booking2 = new Booking();
    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item = new Item();

    @BeforeEach
    public void init() {
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        user2.setName("user2");
        user2.setEmail("u2@user.com");

        item.setName("item1");
        item.setDescription("descr item1");
        item.setAvailable(true);
        item.setOwner(1);

        booking1.setStart(LocalDateTime.now().plusHours(1).withNano(0));
        booking1.setEnd(LocalDateTime.now().plusHours(2).withNano(0));
        booking1.setBookerId(2);
        booking1.setItem(1);
        booking1.setStatus(Status.WAITING);

        booking2.setStart(LocalDateTime.now().plusHours(3).withNano(0));
        booking2.setEnd(LocalDateTime.now().plusHours(4).withNano(0));
        booking2.setBookerId(2);
        booking2.setItem(1);
        booking2.setStatus(Status.WAITING);

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(item);
        entityManager.persist(user2);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.flush();
    }

    @Test
    public void getBookingByIdTest() {
        TypedQuery<Booking> query = entityManager.getEntityManager()
                .createQuery("select b from Booking b join Item i on b.item=i.id where b.id=:booking and " +
                        "(b.bookerId=:user or i.owner=:user)", Booking.class);
        Booking bookingResult = query.setParameter("booking", 1).setParameter("user", 2).getSingleResult();
        Assertions.assertEquals(bookingResult.getBookerId(), booking1.getBookerId());
    }

    @Test
    public void getBookingByUserAndStatusTest() {
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery("select distinct b from Booking b" +
                " join Item i on i.id=b.item and b.bookerId=:id and b.status=:stat and b.bookerId <> i.owner" +
                " order by b.start desc", Booking.class);
        List<Booking> bookingResult =
                query.setParameter("id", 2).setParameter("stat", Status.WAITING).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking2, booking1));
    }

    @Test
    public void getBookingByUserTest() {
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item and b.bookerId=:id and b.bookerId <> i.owner " +
                        "order by b.start desc", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 2).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking2, booking1));
    }

    @Test
    public void getAllBookingsByOwnerTest() {
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item where i.owner=:id order by b.start desc",
                Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 1).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking2, booking1));
    }

    @Test
    public void getAllBookingsByOwnerAndStatusTest() {
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item where i.owner=:id " +
                        "and b.status=:status order by b.start desc", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 1).setParameter("status", Status.WAITING).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking2, booking1));
    }

    @Test
    public void getAllBookingsByOwnerStatusFutureTest() {
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item where i.owner=:id and b.end>:date " +
                        "order by b.start desc", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 1).setParameter("date", LocalDateTime.now()).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking2, booking1));
    }

    @Test
    public void getAllBookingsByOwnerStatusPastTest() {
        booking1.setStart(LocalDateTime.now().minusHours(2).withNano(0));
        booking1.setEnd(LocalDateTime.now().minusHours(1).withNano(0));
        booking2.setStart(LocalDateTime.now().minusHours(4).withNano(0));
        booking2.setEnd(LocalDateTime.now().minusHours(3).withNano(0));
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item where i.owner=:id and b.end<:date " +
                        "order by b.start desc", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 1).setParameter("date", LocalDateTime.now()).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking1, booking2));
    }

    @Test
    public void getAllBookingsByOwnerStatusCurrentTest() {
        booking1.setStart(LocalDateTime.now().minusHours(2).withNano(0));
        booking1.setEnd(LocalDateTime.now().plusHours(1).withNano(0));
        booking2.setStart(LocalDateTime.now().minusHours(4).withNano(0));
        booking2.setEnd(LocalDateTime.now().plusHours(3).withNano(0));
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item where i.owner=:id and b.end>:date " +
                        "and b.start<:date order by b.start desc", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 1).setParameter("date", LocalDateTime.now()).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking1, booking2));
    }

    @Test
    public void getLastBookingByItemTest() {
        booking1.setStart(LocalDateTime.now().minusHours(2).withNano(0));
        booking1.setEnd(LocalDateTime.now().minusHours(1).withNano(0));
        TypedQuery<Booking> query = entityManager.getEntityManager()
                .createQuery("select b from Booking b join Item i on i.id=b.item and i.id=:id and b.end<:date",
                        Booking.class);
        Booking bookingResult =
                query.setParameter("id", 1).setParameter("date", LocalDateTime.now()).getSingleResult();
        Assertions.assertEquals(bookingResult.getBookerId(), booking1.getBookerId());
    }

    @Test
    public void getNextBookingByItemTest() {
        booking1.setStart(LocalDateTime.now().minusHours(2).withNano(0));
        booking1.setEnd(LocalDateTime.now().minusHours(1).withNano(0));
        TypedQuery<Booking> query = entityManager.getEntityManager()
                .createQuery("select b from Booking b join Item i on i.id=b.item and i.id=:id " +
                        "and b.end>:date and b.start>:date", Booking.class);
        Booking bookingResult =
                query.setParameter("id", 1).setParameter("date", LocalDateTime.now()).getSingleResult();
        Assertions.assertEquals(bookingResult.getBookerId(), booking2.getBookerId());
    }

    @Test
    public void getAllBookingsByUserStatusCurrentTest() {
        booking1.setStart(LocalDateTime.now().minusHours(2).withNano(0));
        booking1.setEnd(LocalDateTime.now().plusHours(1).withNano(0));
        booking2.setStart(LocalDateTime.now().minusHours(4).withNano(0));
        booking2.setEnd(LocalDateTime.now().plusHours(3).withNano(0));
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item where b.bookerId=:id and b.end>:date " +
                        "and b.start<:date order by b.start desc", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 2).setParameter("date", LocalDateTime.now()).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking1, booking2));
    }

    @Test
    public void getByBookerAndItemTest() {
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b where b.bookerId=:booker and b.item=:item", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("booker", 2).setParameter("item", 1).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking1, booking2));
    }

    @Test
    public void getCountOfUserBookingTest() {

        int count = bookingRepository.getCountBookingsByUser(2);
        Assertions.assertEquals(count, 2);
    }
}