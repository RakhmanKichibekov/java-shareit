package ru.practicum.shareit.item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDtoDate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private CommentMapper commentMapper;

    private final Item item1 = new Item();
    private final Item item2 = new Item();

    private final User user1 = new User();
    private final User user2 = new User();
    private final Booking booking1 = new Booking();
    private final Booking booking2 = new Booking();
    private final ItemDtoDate itemDto1 = new ItemDtoDate();
    private final ItemDtoDate itemDto2 = new ItemDtoDate();
    private final Comment comment = new Comment();
    private final CommentDto commentDto = new CommentDto();

    @BeforeEach
    public void init() {
        item1.setId(1);
        item1.setAvailable(true);
        item1.setName("item1");
        item1.setDescription("descr of item1");
        item1.setOwner(1);
        item2.setId(2);
        item2.setAvailable(true);
        item2.setName("item2");
        item2.setDescription("descr of item2");
        item2.setOwner(1);
        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        user2.setId(2);
        user2.setName("user2");
        user2.setEmail("u2@user.com");
        booking1.setId(1);
        booking1.setItem(1);
        booking1.setStatus(Status.WAITING);
        booking1.setBookerId(1);
        booking1.setStart(LocalDateTime.now().plusHours(1));
        booking1.setEnd(LocalDateTime.now().minusHours(1));
        booking2.setId(2);
        booking2.setItem(2);
        booking2.setStatus(Status.WAITING);
        booking2.setBookerId(1);
        booking2.setStart(LocalDateTime.now().plusHours(2));
        booking2.setEnd(LocalDateTime.now().minusHours(2));
        itemDto1.setId(item1.getId());
        itemDto1.setOwner(item1.getOwner());
        itemDto1.setAvailable(true);
        itemDto1.setName(item1.getName());
        itemDto1.setDescription(item1.getDescription());
        itemDto1.setLastBooking(booking1);
        itemDto1.setNextBooking(booking1);
        itemDto2.setId(item1.getId());
        itemDto2.setOwner(item2.getOwner());
        itemDto2.setAvailable(true);
        itemDto2.setName(item2.getName());
        itemDto2.setDescription(item2.getDescription());
        itemDto2.setLastBooking(booking1);
        itemDto2.setNextBooking(booking1);
        comment.setId(1);
        comment.setAuthor(2);
        comment.setItem(1);
        comment.setCreated(LocalDateTime.now());
        comment.setText("comment on item1");
        commentDto.setId(1);
        commentDto.setAuthor(2);
        commentDto.setItem(1);
        commentDto.setText("comment on item1");
    }

    @Test
    public void addItemTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item1);
        Item itemResult = itemService.add(item1);
        Assertions.assertEquals(itemResult, item1);
    }

    @Test
    public void updateItemTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(itemRepository.findById(1))
                .thenReturn(Optional.of(item1));
        item2.setId(1);
        item2.setName("upd");
        item2.setDescription("upd descr");
        item2.setOwner(1);
        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item2);
        Item item = new Item();
        item.setId(1);
        item.setName("upd");
        item.setDescription("upd descr");
        item.setOwner(1);
        Item itemResult = itemService.update(item, 1);
        Assertions.assertEquals(itemResult.getName(), item2.getName());
        Assertions.assertEquals(itemResult.getDescription(), item2.getDescription());

    }

    @Test
    public void getItemByIdTest() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item1));
        Item itemResult = itemService.getById(1);
        Assertions.assertEquals(itemResult, item1);
    }

    @Test
    public void getAllItemsTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> list = List.of(item1, item2);
        Page<Item> page = new PageImpl<>(list, pageable, list.size());

        Mockito
                .when(itemRepository.findAllByOwnerOrderById(1, pageable))
                .thenReturn(page);
        Collection<ItemDtoDate> items = itemService.getAll(1, 0, 10);
        List<ItemDtoDate> itemDtoDateList = (List<ItemDtoDate>) items;
        Assertions.assertEquals(items.size(), 2);
        Assertions.assertEquals(itemDtoDateList.get(0).getId(), 1);
        Assertions.assertEquals(itemDtoDateList.get(1).getId(), 2);
        Assertions.assertEquals(itemDtoDateList.get(0).getName(), item1.getName());
        Assertions.assertEquals(itemDtoDateList.get(1).getName(), item2.getName());
    }

    @Test
    public void getItemWithDateTest() {

        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user2));
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item1));

        Mockito
                .when(commentRepository.findAllByItem(Mockito.anyInt()))
                .thenReturn(Set.of(comment));
        Mockito
                .when(commentMapper.toDto(Mockito.any(Comment.class), Mockito.any(User.class)))
                .thenReturn(commentDto);

        Mockito
                .when(bookingRepository.getLastBooking(1, LocalDateTime.now().withNano(0)))
                .thenReturn(booking1);

        ItemDtoDate itemDtoDate = itemService.getItemDate(1, LocalDateTime.now().withNano(0), 2);
        Assertions.assertEquals(itemDtoDate.getId(), item1.getId());
        Assertions.assertEquals(itemDtoDate.getName(), item1.getName());
        Assertions.assertEquals(itemDtoDate.getDescription(), item1.getDescription());
        Assertions.assertEquals(itemDtoDate.getAvailable(), item1.getAvailable());
        Assertions.assertEquals(itemDtoDate.getOwner(), item1.getOwner());
        Assertions.assertEquals(itemDtoDate.getComments().size(), 1);
    }

    @Test
    public void addCommentTest() {
        Mockito
                .when(bookingRepository.getByBookerAndItem(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(booking1, booking2));
        Mockito
                .when(commentRepository.save(comment))
                .thenReturn(comment);
        Mockito
                .when(commentMapper.toDto(comment, user2))
                .thenReturn(commentDto);
        Mockito
                .when(commentMapper.toComment(commentDto))
                .thenReturn(comment);
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user2));
        CommentDto commentDtoResult = itemService.addComment(commentDto, 1, 2);
        Assertions.assertEquals(commentDtoResult, commentDto);
    }

    @Test
    public void getUserTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user2));
        User userResult = itemService.getUser(2);
        Assertions.assertEquals(userResult, user2);
    }

    @Test
    public void deleteItemTest() {
        itemService.delete(1, 1);
        Mockito
                .verify(itemRepository, Mockito.times(1))
                .deleteById(1);
    }
}
