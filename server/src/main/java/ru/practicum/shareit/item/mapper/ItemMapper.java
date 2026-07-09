package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());
        dto.setRequestId(item.getRequestId());
        return dto;
    }

    public static Item toItem(ItemDto dto, Long ownerId) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwnerId(ownerId);
        item.setRequestId(dto.getRequestId());
        return item;
    }

    public static ItemDto toItemDto(Item item, List<CommentDto> comments) {
        ItemDto dto = toItemDto(item);
        dto.setComments(comments);
        return dto;
    }
}
