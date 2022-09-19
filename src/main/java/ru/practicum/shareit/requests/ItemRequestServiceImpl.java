package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService{

    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    @PersistenceContext
    public EntityManager em;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  UserRepository userRepository, ItemRepository itemRepository){
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public ItemRequestDto createItemRequest(Optional<Long> idUser, ItemRequestDto itemRequestDto) throws ValidationException {
        User user = validationUser(idUser);
        if (itemRequestDto.getDescription().isBlank())
            throw new ValidationException ("Запрос указан как null! createItemRequest()");
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        log.info("Создан запрос id: {}", itemRequest.getId());
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }
    public User validationUser (Optional<Long> idUser) {
        if (!idUser.isPresent()) throw new NoSuchElementException("Отсутствует id владельца! validationUser()");
        Optional<User> user = userRepository.findById(idUser.get());
        if (!user.isPresent())
            throw new NoSuchElementException("Указанный пользователь не найден! validationUser()");
        return user.get();
    }
    public List<ItemRequestDto> findAllItemRequest(Optional<Long> idUser) throws ValidationException {
        User user = validationUser(idUser);
        List<ItemRequestDto> list = ItemRequestMapper.toListItemRequestDto(
                itemRequestRepository.findByRequestor_IdOrderByCreatedDesc(user.getId()));
        for (ItemRequestDto itemRequestDto: list){
            itemRequestDto.setItems(ItemMapper.toListItemDto(itemRepository.findByRequest_IdOrderByCreated(itemRequestDto.getId()).get()));
        }
        log.info("Получен список запросов пользователя: {}", user.getName());
        return list;
    }

    public ItemRequestDto findItemRequestById (Optional<Long> idUser, Optional<Long> id) {
        validationUser(idUser);
        if (!id.isPresent())
            throw new NoSuchElementException("Не правильно задан id запроса вещи! findItemRequestById()");
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(id.get());
        if (!itemRequest.isPresent())
            throw new NoSuchElementException("Такого запроса вещи не существует! findItemRequestById()");
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest.get());
        itemRequestDto.setItems(ItemMapper.toListItemDto(itemRepository.findByRequest_IdOrderByCreated(id.get()).get()));
        log.info("Получен запрос на вещь id: {}", itemRequestDto.getId());
        return itemRequestDto;
    }
    public List<ItemRequestDto> findItemRequestPageable (Optional<Long> idUser,
                                                         Optional<Integer> from, Optional<Integer> size) throws ValidationException {
        User user = validationUser(idUser);
        if (!from.isPresent() || !size.isPresent()) return Collections.emptyList();
        if (from.get() < 0 || size.get() <= 0)
            throw new ValidationException("Параметры from, size заданы не верно! findItemRequestPageable()");
        List<ItemRequest> requestList = em.createQuery("SELECT i FROM ItemRequest i "
                        + "WHERE i.requestor.id <> ?1 ORDER BY i.created DESC", ItemRequest.class)
                .setParameter(1, idUser.get())
                .setFirstResult(from.get())
                .setMaxResults(size.get())
                .getResultList();
        List<ItemRequestDto> itemRequestDtoList = ItemRequestMapper.toListItemRequestDto(requestList);
        for (ItemRequestDto itemRequest: itemRequestDtoList) {
            if (itemRepository.findByRequest_IdOrderByCreated(itemRequest.getId()).isPresent())
                itemRequest.setItems(ItemMapper.toListItemDto(itemRepository.findByRequest_IdOrderByCreated(itemRequest.getId()).get()));
        }
        log.info("Пользователь {} получил список запросов.", user.getName());
        return itemRequestDtoList;
    }
}