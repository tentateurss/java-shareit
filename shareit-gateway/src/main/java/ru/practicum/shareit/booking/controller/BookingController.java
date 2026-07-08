package ru.practicum.shareit.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.constants.ShareItConstants;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(ShareItConstants.HEADER_USER_ID) long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                              @RequestParam(name = "from", required = false) @PositiveOrZero Integer from,
                                              @RequestParam(name = "size", required = false) @Positive Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);

        if (from == null || size == null) {
            return bookingClient.findByBooker(userId, stateParam);
        }
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(ShareItConstants.HEADER_USER_ID) long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(ShareItConstants.HEADER_USER_ID) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(ShareItConstants.HEADER_USER_ID) long ownerId,
                                          @PathVariable Long bookingId,
                                          @RequestParam boolean approved) {
        log.info("Approving booking {}, ownerId={}, approved={}", bookingId, ownerId, approved);
        return bookingClient.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwner(@RequestHeader(ShareItConstants.HEADER_USER_ID) long ownerId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Get owner bookings with state {}, ownerId={}", state, ownerId);
        return bookingClient.findByOwner(ownerId, state);
    }
}