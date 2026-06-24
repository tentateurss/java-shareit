package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // методы для арендатора
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start <= ?2 and b.end >= ?2 order by b.start desc")
    List<Booking> findCurrentByBookerId(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    // методы для владельца
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    @Query("select b from Booking b where b.item.ownerId = ?1 and b.start <= ?2 and b.end >= ?2 order by b.start desc")
    List<Booking> findCurrentByOwnerId(Long ownerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);
    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    // методы для получения последнего и следующего бронирования вещи
    Booking findFirstByItemIdAndStatusAndStartLessThanEqualOrderByStartDesc(Long itemId, BookingStatus status, LocalDateTime now);
    Booking findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Long itemId, BookingStatus status, LocalDateTime now);

    // для проверки, арендовал ли пользователь вещь
    List<Booking> findAllByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now);
}