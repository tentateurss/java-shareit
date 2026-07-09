package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.ShareItConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(ShareItConstants.HEADER_USER_ID) Long userId,
                                 @RequestBody ItemRequestDto dto) {
        return requestService.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestDto> findByRequester(@RequestHeader(ShareItConstants.HEADER_USER_ID) Long userId) {
        return requestService.findByRequester(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findFromOthers(@RequestHeader(ShareItConstants.HEADER_USER_ID) Long userId) {
        return requestService.findFromOthers(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@RequestHeader(ShareItConstants.HEADER_USER_ID) Long userId,
                                   @PathVariable Long requestId) {
        return requestService.findById(userId, requestId);
    }
}
