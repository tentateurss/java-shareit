package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(Long userId, BookingRequestDto dto);
    BookingResponseDto approve(Long ownerId, Long bookingId, boolean approved);
    BookingResponseDto findById(Long userId, Long bookingId);
    List<BookingResponseDto> findByBooker(Long bookerId, String state);
    List<BookingResponseDto> findByOwner(Long ownerId, String state);
}
