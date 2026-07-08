package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

public class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFoundTest() {
        NotFoundException ex = new NotFoundException("Объект не найден");
        Map<String, String> response = errorHandler.handleNotFound(ex);
        assertThat(response).containsEntry("error", "Объект не найден");
    }

    @Test
    void handleValidationTest() {
        ValidationException ex = new ValidationException("Ошибка валидации");
        Map<String, String> response = errorHandler.handleValidation(ex);
        assertThat(response).containsEntry("error", "Ошибка валидации");
    }

    @Test
    void handleDuplicateTest() {
        DuplicatedDataException ex = new DuplicatedDataException("Конфликт данных");
        Map<String, String> response = errorHandler.handleDuplicate(ex);
        assertThat(response).containsEntry("error", "Конфликт данных");
    }

    @Test
    void handleForbiddenTest() {
        ForbiddenException ex = new ForbiddenException("Доступ запрещен");
        Map<String, String> response = errorHandler.handleForbidden(ex);
        assertThat(response).containsEntry("error", "Доступ запрещен");
    }
}