package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.ShareItConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(ShareItConstants.HEADER_USER_ID) long userId,
                                         @RequestBody @Valid ItemRequestDto dto) {
        log.info("Creating request {}, userId={}", dto, userId);
        return requestClient.create(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> findByRequester(@RequestHeader(ShareItConstants.HEADER_USER_ID) long userId) {
        log.info("Get requests for requester userId={}", userId);
        return requestClient.findByRequester(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findFromOthers(@RequestHeader(ShareItConstants.HEADER_USER_ID) long userId) {
        log.info("Get requests from other users, userId={}", userId);
        return requestClient.findFromOthers(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(ShareItConstants.HEADER_USER_ID) long userId,
                                           @PathVariable Long requestId) {
        log.info("Get request {}, userId={}", requestId, userId);
        return requestClient.findById(userId, requestId);
    }
}