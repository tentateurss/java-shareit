package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public List<ItemDto> findAllByOwnerId(Long ownerId) {
        return itemStorage.findAllByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findById(Long id) {
        Item item = itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + id + " не найдена"));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        userStorage.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + ownerId + " не найден"));

        Item item = ItemMapper.toItem(itemDto, ownerId);
        Item created = itemStorage.create(item);
        return ItemMapper.toItemDto(created);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        Item existingItem = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        if (!existingItem.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("Редактировать вещь может только владелец");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updated = itemStorage.update(existingItem);
        return ItemMapper.toItemDto(updated);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}