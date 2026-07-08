package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestDto dto);

    List<ItemRequestDto> findByRequester(Long userId);

    List<ItemRequestDto> findFromOthers(Long userId);

    ItemRequestDto findById(Long userId, Long requestId);
}