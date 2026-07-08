package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // методы для арендатора
    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 ORDER BY b.start DESC")
    List<Booking> findByBooker(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.start <= ?2 AND b.end >= ?2 ORDER BY b.start DESC")
    List<Booking> findCurrentByBooker(Long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findPastByBooker(Long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findFutureByBooker(Long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findByBookerAndStatus(Long bookerId, BookingStatus status);

    // методы для владельца
    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = ?1 ORDER BY b.start DESC")
    List<Booking> findByOwner(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = ?1 AND b.start <= ?2 AND b.end >= ?2 ORDER BY b.start DESC")
    List<Booking> findCurrentByOwner(Long ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findPastByOwner(Long ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findFutureByOwner(Long ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findByOwnerAndStatus(Long ownerId, BookingStatus status);

    // методы для получения последнего и следующего бронирования вещи
    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.status = ?2 AND b.start <= ?3 ORDER BY b.start DESC LIMIT 1")
    Booking findLastBooking(Long itemId, BookingStatus status, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.status = ?2 AND b.start > ?3 ORDER BY b.start ASC LIMIT 1")
    Booking findNextBooking(Long itemId, BookingStatus status, LocalDateTime now);

    // для проверки, арендовал ли пользователь вещь
    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.item.id = ?2 AND b.end < ?3")
    List<Booking> findCompletedByBookerAndItem(Long bookerId, Long itemId, LocalDateTime now);
}