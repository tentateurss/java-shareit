package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.ShareItConstants;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findAllByOwnerId(@RequestHeader(ShareItConstants.HEADER_USER_ID) Long ownerId) {
        return itemService.findAllByOwnerId(ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@RequestHeader(ShareItConstants.HEADER_USER_ID) Long userId,
                            @PathVariable Long id) {
        return itemService.findById(id, userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(value = ShareItConstants.HEADER_USER_ID, required = true) Long ownerId,
                          @RequestBody ItemDto itemDto) {
        return itemService.create(ownerId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(ShareItConstants.HEADER_USER_ID) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(ShareItConstants.HEADER_USER_ID) Long ownerId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(ownerId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}