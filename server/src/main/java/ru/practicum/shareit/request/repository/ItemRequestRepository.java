package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT r FROM ItemRequest r WHERE r.requesterId = :userId ORDER BY r.created DESC")
    List<ItemRequest> findByRequesterId(@Param("userId") Long requesterId);

    @Query("SELECT r FROM ItemRequest r WHERE r.requesterId <> :userId ORDER BY r.created DESC")
    List<ItemRequest> findFromOthers(@Param("userId") Long requesterId);
}
