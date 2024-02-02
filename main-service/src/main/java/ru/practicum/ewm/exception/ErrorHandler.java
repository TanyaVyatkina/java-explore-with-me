package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.dto.ApiError;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.error(e.getMessage());
        return createError(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleRuntimeException(final RuntimeException e) {
        log.error(e.getMessage(), e);
        return createError(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.error(e.getMessage());
        return createError(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        log.error(e.getMessage());
        return createError(e, HttpStatus.CONFLICT);
    }

    ApiError createError(RuntimeException ex, HttpStatus status) {
        ApiError error = new ApiError();
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(status);
        List<String> errors = Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
        error.setErrors(errors);
        if (ex.getCause() != null) {
            error.setReason(ex.getCause().toString());
        }
        return error;
    }
}

