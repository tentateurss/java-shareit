package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constants.ShareItConstants;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@RequestHeader(ShareItConstants.HEADER_USER_ID) Long userId,
                                     @RequestBody BookingRequestDto dto) {
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@RequestHeader(ShareItConstants.HEADER_USER_ID) Long ownerId,
                                      @PathVariable Long bookingId,
                                      @RequestParam boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findById(@RequestHeader(ShareItConstants.HEADER_USER_ID) Long userId,
                                       @PathVariable Long bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> findByBooker(@RequestHeader(ShareItConstants.HEADER_USER_ID) Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findByOwner(@RequestHeader(ShareItConstants.HEADER_USER_ID) Long ownerId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findByOwner(ownerId, state);
    }
}