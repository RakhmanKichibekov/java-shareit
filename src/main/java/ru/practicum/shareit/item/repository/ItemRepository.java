package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.Optional;
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner_IdOrderById(Long idUser);
    @Query("select i from Item i " +
            "where i.request.id = ?1") // order by i.request.created desc
    Optional<List<Item>> findByRequest_IdOrderByCreated(Long id);
    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')) " +
            "and i.available is true")
    List<Item> searchListItem(String text);
}
