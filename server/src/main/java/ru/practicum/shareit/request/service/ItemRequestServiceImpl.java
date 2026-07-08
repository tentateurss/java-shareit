package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new ValidationException("Описание запроса не может быть пустым");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequesterId(userId);
        request.setCreated(LocalDateTime.now());

        ItemRequest saved = requestRepository.save(request);
        return ItemRequestMapper.toItemRequestDto(saved);
    }

    @Override
    public List<ItemRequestDto> findByRequester(Long userId) {
        checkUserExists(userId);


        return requestRepository.findByRequesterId(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .peek(this::enrichWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findFromOthers(Long userId) {
        checkUserExists(userId);

        return requestRepository.findFromOthers(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .peek(this::enrichWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto findById(Long userId, Long requestId) {
        checkUserExists(userId);

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id=" + requestId + " не найден"));

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);
        enrichWithItems(dto);
        return dto;
    }

    // вспомогательный метод проверки пользователя
    private void checkUserExists(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    // метод, который находит вещи, созданные под этот запрос и добавляет их в DTO
    private void enrichWithItems(ItemRequestDto dto) {
        List<ItemDto> items = itemRepository.findAllByRequestId(dto.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        dto.setItems(items);
    }
}