package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");

        user = new User();
        user.setId(2L);
        user.setName("Пользователь");
        user.setEmail("user@test.com");

        item = new Item();
        item.setId(10L);
        item.setName("Отвертка");
        item.setDescription("Электрическая");
        item.setAvailable(true);
        item.setOwnerId(owner.getId());

        itemDto = new ItemDto();
        itemDto.setId(10L);
        itemDto.setName("Отвертка");
        itemDto.setDescription("Электрическая");
        itemDto.setAvailable(true);
    }

    @Test
    void createShouldSaveItemWhenValid() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(owner.getId(), itemDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(itemDto.getName());
    }

    @Test
    void createShouldThrowValidationExceptionWhenNameIsEmpty() {
        itemDto.setName("");
        assertThatThrownBy(() -> itemService.create(owner.getId(), itemDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Название не может быть пустым");
    }

    @Test
    void updateShouldModifyFieldsWhenUserIsOwner() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Супер Отвертка");

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        ItemDto result = itemService.update(owner.getId(), item.getId(), updateDto);

        assertThat(result.getName()).isEqualTo("Супер Отвертка");
    }

    @Test
    void updateShouldThrowNotFoundExceptionWhenUserIsNotOwner() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.update(99L, item.getId(), itemDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Редактировать вещь может только владелец");
    }

    @Test
    void findByIdShouldReturnItemWithBookingsForOwner() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(Collections.emptyList());

        Booking lastBooking = new Booking();
        lastBooking.setId(100L);
        lastBooking.setBooker(user);

        when(bookingRepository.findLastBooking(eq(item.getId()), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(lastBooking);

        ItemDto result = itemService.findById(item.getId(), owner.getId());

        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getLastBooking().getId()).isEqualTo(100L);
    }

    @Test
    void searchShouldReturnEmptyListWhenTextIsBlank() {
        List<ItemDto> result = itemService.search("");
        assertThat(result).isEmpty();
    }

    @Test
    void searchShouldReturnListWhenTextMatches() {
        when(itemRepository.search("отвертка")).thenReturn(List.of(item));

        List<ItemDto> result = itemService.search("отвертка");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo(item.getName());
    }

    @Test
    void addCommentShouldSaveCommentWhenUserHasApprovedBooking() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Классный инструмент!");

        Booking completedBooking = new Booking();
        completedBooking.setId(50L);
        completedBooking.setStatus(BookingStatus.APPROVED);

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setText(commentDto.getText());
        savedComment.setAuthor(user);
        savedComment.setItem(item);
        savedComment.setCreated(LocalDateTime.now());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findCompletedByBookerAndItem(eq(user.getId()), eq(item.getId()), any(LocalDateTime.class)))
                .thenReturn(List.of(completedBooking));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto result = itemService.addComment(user.getId(), item.getId(), commentDto);

        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo(commentDto.getText());
        assertThat(result.getAuthorName()).isEqualTo(user.getName());
    }

    @Test
    void addCommentShouldThrowValidationExceptionWhenNoApprovedBooking() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Плохой инструмент");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findCompletedByBookerAndItem(eq(user.getId()), eq(item.getId()), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> itemService.addComment(user.getId(), item.getId(), commentDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Вы не можете оставить отзыв на эту вещь");
    }

    @Test
    void searchShouldReturnEmptyListWhenTextIsOnlySpaces() {
        List<ItemDto> result = itemService.search("   ");
        assertThat(result).isEmpty();
    }

    @Test
    void updateShouldNotChangeFieldsIfDtoIsEmpty() {
        long ownerId = 1L;
        long itemId = 10L;

        Item item = new Item();
        item.setId(itemId);
        item.setOwnerId(ownerId);
        item.setName("Старое имя");
        item.setDescription("Старое описание");
        item.setAvailable(true);

        ItemDto emptyDto = new ItemDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        ItemDto result = itemService.update(ownerId, itemId, emptyDto);

        assertThat(result.getName()).isEqualTo("Старое имя");
        assertThat(result.getDescription()).isEqualTo("Старое описание");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void searchEmptyTextReturnsEmptyList() {
        assertThat(itemService.search("")).isEmpty();
        verify(itemRepository, never()).search(anyString());
    }
}