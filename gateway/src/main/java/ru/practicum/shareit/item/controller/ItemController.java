package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.ShareItConstants;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(ShareItConstants.HEADER_USER_ID) long userId,
                                             @RequestBody @Valid ItemDto dto) {
        log.info("Creating item {}, userId={}", dto, userId);
        return itemClient.createItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(ShareItConstants.HEADER_USER_ID) long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemDto dto) {
        log.info("Updating item {}, itemId={}, userId={}", dto, itemId, userId);
        return itemClient.updateItem(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(ShareItConstants.HEADER_USER_ID) long userId,
                                          @PathVariable long itemId) {
        log.info("Get item itemId={}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader(ShareItConstants.HEADER_USER_ID) long userId) {
        log.info("Get owner items, userId={}", userId);
        return itemClient.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(ShareItConstants.HEADER_USER_ID) long userId,
                                              @RequestParam String text) {
        log.info("Search items by text='{}', userId={}", text, userId);
        return itemClient.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(ShareItConstants.HEADER_USER_ID) long userId,
                                                @PathVariable long itemId,
                                                @RequestBody @Valid CommentDto dto) {
        log.info("Creating comment {} for item itemId={}, userId={}", dto, itemId, userId);
        return itemClient.createComment(userId, itemId, dto);
    }
}