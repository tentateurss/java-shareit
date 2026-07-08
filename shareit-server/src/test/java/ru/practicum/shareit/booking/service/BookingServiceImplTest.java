package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private BookingRequestDto requestDto;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(10L);
        booker.setName("Booker");
        booker.setEmail("booker@test.com");

        owner = new User();
        owner.setId(20L);
        owner.setName("Owner");
        owner.setEmail("owner@test.com");

        item = new Item();
        item.setId(100L);
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwnerId(owner.getId());

        requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    // тесты метода create

    @Test
    void createShouldSaveBookingWhenValid() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(1L);
            return b;
        });

        BookingResponseDto result = bookingService.create(booker.getId(), requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createShouldThrowValidationExceptionWhenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(booker.getId(), requestDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Вещь недоступна для бронирования");
    }

    @Test
    void createShouldThrowNotFoundExceptionWhenOwnerTriesToBookOwnItem() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(owner.getId(), requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Нельзя забронировать свою вещь");
    }

    @Test
    void createShouldThrowValidationExceptionWhenDatesAreInvalid() {
        requestDto.setEnd(requestDto.getStart().minusHours(1)); // Конец до начала
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(booker.getId(), requestDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Дата окончания должна быть позже даты начала");
    }

    // тесты метода approve

    @Test
    void approveShouldStatusChangeWhenOwnerApproves() {
        Booking booking = new Booking();
        booking.setId(1000L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingResponseDto result = bookingService.approve(owner.getId(), booking.getId(), true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveShouldThrowValidationExceptionWhenNotOwner() {
        Booking booking = new Booking();
        booking.setId(1000L);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approve(booker.getId(), booking.getId(), true))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Подтверждать может только владелец");
    }

    // тесты метода findById

    @Test
    void findByIdShouldReturnBookingForBookerOrOwner() {
        Booking booking = new Booking();
        booking.setId(5L);
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.findById(5L)).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.findById(booker.getId(), 5L);
        assertThat(result.getId()).isEqualTo(5L);
    }

    @Test
    void findByIdShouldThrowNotFoundExceptionForStranger() {
        Booking booking = new Booking();
        booking.setId(5L);
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.findById(5L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.findById(999L, 5L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Доступ запрещён");
    }

    // тесты метода findByBooker

    @Test
    void findByBookerShouldCallCorrectRepositoryMethodsForStates() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));

        // состояние "ALL"
        bookingService.findByBooker(booker.getId(), "ALL");
        verify(bookingRepository, times(1)).findByBooker(booker.getId());

        // состояние "FUTURE"
        bookingService.findByBooker(booker.getId(), "FUTURE");
        verify(bookingRepository, times(1)).findFutureByBooker(eq(booker.getId()), any(LocalDateTime.class));

        // состояние "WAITING"
        bookingService.findByBooker(booker.getId(), "WAITING");
        verify(bookingRepository, times(1)).findByBookerAndStatus(booker.getId(), BookingStatus.WAITING);
    }

    // тесты метода findByOwner

    @Test
    void findByOwnerShouldCallCorrectRepositoryMethodsForStates() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        // состояние "PAST"
        bookingService.findByOwner(owner.getId(), "PAST");
        verify(bookingRepository, times(1)).findPastByOwner(eq(owner.getId()), any(LocalDateTime.class));

        // состояние "CURRENT"
        bookingService.findByOwner(owner.getId(), "CURRENT");
        verify(bookingRepository, times(1)).findCurrentByOwner(eq(owner.getId()), any(LocalDateTime.class));
    }

    // тесты для полного покрытия ветвлений

    @Test
    void findByBookerShouldCallCorrectMethodsForRemainingStates() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));

        // состояние "PAST"
        bookingService.findByBooker(booker.getId(), "PAST");
        verify(bookingRepository, times(1)).findPastByBooker(eq(booker.getId()), any(LocalDateTime.class));

        // состояние "CURRENT"
        bookingService.findByBooker(booker.getId(), "CURRENT");
        verify(bookingRepository, times(1)).findCurrentByBooker(eq(booker.getId()), any(LocalDateTime.class));

        // состояние "REJECTED"
        bookingService.findByBooker(booker.getId(), "REJECTED");
        verify(bookingRepository, times(1)).findByBookerAndStatus(booker.getId(), BookingStatus.REJECTED);
    }

    @Test
    void findByOwnerShouldCallCorrectMethodsForRemainingStates() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        // состояние "ALL"
        bookingService.findByOwner(owner.getId(), "ALL");
        verify(bookingRepository, times(1)).findByOwner(owner.getId());

        // состояние "FUTURE"
        bookingService.findByOwner(owner.getId(), "FUTURE");
        verify(bookingRepository, times(1)).findFutureByOwner(eq(owner.getId()), any(LocalDateTime.class));

        // состояние "WAITING"
        bookingService.findByOwner(owner.getId(), "WAITING");
        verify(bookingRepository, times(1)).findByOwnerAndStatus(owner.getId(), BookingStatus.WAITING);

        // состояние "REJECTED"
        bookingService.findByOwner(owner.getId(), "REJECTED");
        verify(bookingRepository, times(1)).findByOwnerAndStatus(owner.getId(), BookingStatus.REJECTED);
    }

    @Test
    void approveShouldThrowValidationExceptionWhenAlreadyApproved() {
        Booking booking = new Booking();
        booking.setId(1000L);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approve(owner.getId(), booking.getId(), true))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Бронирование уже обработано");
    }
}