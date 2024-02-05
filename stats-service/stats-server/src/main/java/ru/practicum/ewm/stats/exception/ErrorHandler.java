package ru.practicum.ewm.stats.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException e) {
        log.error("Ошибка валидации: {}", e.getMessage());
        return createError(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleRuntimeException(final RuntimeException e) {
        log.error(e.getMessage(), e);
        return createError(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    ApiError createError(RuntimeException ex, HttpStatus status) {
        ApiError error = new ApiError();
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(status);
        error.setErrors(List.of(ex.getStackTrace()));
        if (ex.getCause() != null) {
            error.setReason(ex.getCause().toString());
        }
        return error;
    }
}

