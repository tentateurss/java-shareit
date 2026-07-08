package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Иван");
        user.setEmail("ivan@test.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Иван");
        userDto.setEmail("ivan@test.com");
    }

    @Test
    void findAllShouldReturnUserList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void findByIdShouldReturnUserWhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findByIdShouldThrowNotFoundExceptionWhenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.ofNullable(null));

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id=99 не найден");
    }

    @Test
    void createShouldSaveUserWhenValid() {
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.create(userDto);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void createShouldThrowValidationExceptionWhenEmailIsInvalid() {
        userDto.setEmail("invalid-email");

        assertThatThrownBy(() -> userService.create(userDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Некорректный формат email");
    }

    @Test
    void createShouldThrowDuplicatedDataExceptionWhenEmailExists() {
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(userDto))
                .isInstanceOf(DuplicatedDataException.class)
                .hasMessageContaining("Email уже используется");
    }

    @Test
    void updateShouldModifyFieldsCorrectly() {
        UserDto updateDto = new UserDto();
        updateDto.setName("Новое Имя");
        updateDto.setEmail("new@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDto result = userService.update(1L, updateDto);

        assertThat(result.getName()).isEqualTo("Новое Имя");
        assertThat(result.getEmail()).isEqualTo("new@test.com");
    }

    @Test
    void deleteShouldCallRepository() {
        userService.delete(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}