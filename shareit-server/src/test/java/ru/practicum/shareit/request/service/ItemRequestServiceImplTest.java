package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository; // Импортируй твой ItemRepository
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    @Test
    void createShouldSaveRequestWhenUserExists() {
        long userId = 1L;
        ItemRequestDto inputDto = new ItemRequestDto();
        inputDto.setDescription("Нужна отвертка");

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        ItemRequestDto result = requestService.create(userId, inputDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getDescription()).isEqualTo("Нужна отвертка");

        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void createShouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        long userId = 999L;
        ItemRequestDto inputDto = new ItemRequestDto();
        inputDto.setDescription("Нужна отвертка");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.create(userId, inputDto))
                .isInstanceOf(NotFoundException.class);

        verify(requestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void findByRequesterShouldReturnListWhenUserExists() {
        long userId = 1L;
        long requestId = 10L;
        User user = new User();
        user.setId(userId);

        ItemRequest request = new ItemRequest();
        request.setId(requestId);
        request.setDescription("Нужна дрель");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequesterId(userId)).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = requestService.findByRequester(userId);

        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getId()).isEqualTo(requestId);
        assertThat(result.getFirst().getDescription()).isEqualTo("Нужна дрель");

        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, times(1)).findByRequesterId(userId);
        verify(itemRepository, times(1)).findAllByRequestId(requestId);
    }

    @Test
    void findFromOthersShouldReturnList() {
        long userId = 1L;
        long requestId = 20L;
        User user = new User();
        user.setId(userId);

        ItemRequest request = new ItemRequest();
        request.setId(requestId);
        request.setDescription("Нужен перфоратор");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findFromOthers(userId)).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = requestService.findFromOthers(userId);

        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getId()).isEqualTo(requestId);
        assertThat(result.getFirst().getDescription()).isEqualTo("Нужен перфоратор");

        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, times(1)).findFromOthers(userId);
        verify(itemRepository, times(1)).findAllByRequestId(requestId);
    }

    @Test
    void findByIdShouldReturnRequestWhenExists() {
        long userId = 1L;
        long requestId = 10L;
        User user = new User();
        user.setId(userId);

        ItemRequest request = new ItemRequest();
        request.setId(requestId);
        request.setDescription("Нужен триммер");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(Collections.emptyList());

        ItemRequestDto result = requestService.findById(userId, requestId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(requestId);
        assertThat(result.getDescription()).isEqualTo("Нужен триммер");

        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, times(1)).findById(requestId);
        verify(itemRepository, times(1)).findAllByRequestId(requestId);
    }

    @Test
    void findByIdShouldThrowNotFoundExceptionWhenRequestDoesNotExist() {
        long userId = 1L;
        long requestId = 999L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.findById(userId, requestId))
                .isInstanceOf(NotFoundException.class);

        verify(requestRepository, times(1)).findById(requestId);
    }
}