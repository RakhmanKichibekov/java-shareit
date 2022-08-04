package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.AccessIsDeniedException;
import ru.practicum.shareit.item.exception.CommentValidateException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    public List<ItemDto> findAllByUserId(Integer userId) {
        log.info("Получение списка вещей пользователя с id {}.", userId);
        userService.findById(userId);
        List<ItemDto> list = itemRepository.findByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        for (ItemDto itemDto : list) {
            addBookings(itemDto);
            addComments(itemDto);
        }
        return list;
    }

    @Override
    public ItemDto findById(Integer userId, Integer itemId) {
        log.info("Найти вещь c id {} пользователем с id {}.", itemId, userId);
        userService.findUserOrException(userId);
        Item item = findItemOrException(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            addBookings(itemDto);
        }
        addComments(itemDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> search(String text) {
        log.info("Найти список вещей по тексту {}.", text);
        List<ItemDto> list = new ArrayList<>();
        if (!text.isBlank()) {
            for (Item item : itemRepository.search(text)) {
                list.add(ItemMapper.toItemDto(item));
            }
        }
        return list;
    }

    @Override
    public ItemDto add(Integer userId, ItemDto itemDto) {
        log.info("Добавление вещи с name {} пользователем c id {}.", itemDto.getName(), userId);
        User user = userService.findUserOrException(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto change(Integer userId, Integer itemId, ItemDto itemDto) {
        log.info("Изменение вещи c id {} пользователем с id {}.", itemId, userId);
        User user = userService.findUserOrException(userId);
        Item oldItem = findItemOrException(itemId);
        if (!oldItem.getOwner().getId().equals(user.getId())) {
            log.warn("Редактирование вещи с id {} пользователем с id {}.", itemId, userId);
            throw new AccessIsDeniedException("Недостаточно прав для выполнения операции.");
        }
        Item newItem = ItemMapper.toItem(itemDto);
        if (newItem.getName() != null) {
            oldItem.setName(itemDto.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Override
    public ItemDto deleteById(Integer userId, Integer id) {
        log.info("Удаление вещи c id {} пользователем с id {}.", id, userId);
        User user = userService.findUserOrException(userId);
        ItemDto item = findById(userId, id);
        if (!user.getId().equals(item.getOwner().getId())) {
            log.warn("Редактирование вещи с id {} пользователем с id {}.", id, userId);
            throw new AccessIsDeniedException("Недостаточно прав для выполнения операции.");
        }
        itemRepository.deleteById(id);
        return item;
    }

    @Override
    public CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        log.info("Добавить комментарий к вещи с id {} пользователем c id {}.", itemId, userId);
        User user = userService.findUserOrException(userId);
        Item item = findItemOrException(itemId);
        Optional<Booking> booking = bookingRepository.findPastByBooker(userId,
                        LocalDateTime.now()).stream()
                .filter(b -> b.getItem().getId().equals(itemId))
                .findFirst();
        if (booking.isEmpty()) {
            throw new CommentValidateException("Комментарий не может быть добавлен.");
        }
        return CommentMapper.toCommentDto(commentRepository.save(
                new Comment(
                        null,
                        commentDto.getText(),
                        item,
                        user,
                        LocalDateTime.now())));
    }

    @Override
    public Item findItemOrException(Integer id) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            throw new ItemNotFoundException("Вещь с id " + id + " не найдена.");
        } else {
            return item.get();
        }
    }

    private void addBookings(ItemDto itemDto) {
        Booking lastBooking = bookingRepository.getLastBooking(itemDto.getId(), LocalDateTime.now());
        if (lastBooking != null) {
            itemDto.setLastBooking(new ItemDto.Booking(lastBooking.getId(), lastBooking.getBooker().getId()));
        }
        Booking nextBooking = bookingRepository.getNextBooking(itemDto.getId(), LocalDateTime.now());
        if (nextBooking != null) {
            itemDto.setNextBooking(new ItemDto.Booking(nextBooking.getId(), nextBooking.getBooker().getId()));
        }
    }

    private void addComments(ItemDto itemDto) {
        List<ItemDto.Comment> comments = commentRepository.findByItemId(itemDto.getId())
                .stream()
                .map(c -> new ItemDto.Comment(c.getId(), c.getText(), c.getAuthor().getName(), c.getCreated()))
                .collect(Collectors.toList());
        itemDto.setComments(comments);
    }
}