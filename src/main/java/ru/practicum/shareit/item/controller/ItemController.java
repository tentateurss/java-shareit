package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.findAllByOwnerId(ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable Long id) {
        return itemService.findById(id);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long ownerId,
                          @RequestBody ItemDto itemDto) {
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(ownerId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}