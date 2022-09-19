package ru.practicum.shareit.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("select i from ItemRequest i where i.id = ?1")
    Optional<ItemRequest> findById(Long id);
    List<ItemRequest> findByRequestor_IdOrderByCreatedDesc(Long idUser);
}
