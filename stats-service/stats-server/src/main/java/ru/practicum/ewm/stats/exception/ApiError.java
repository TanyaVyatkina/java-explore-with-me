package ru.practicum.ewm.stats.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ApiError {
    private HttpStatus status;
    private String message;
    private String reason;
    private List<StackTraceElement> errors;
    private LocalDateTime timestamp;
}
