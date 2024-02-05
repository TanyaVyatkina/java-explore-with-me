package ru.practicum.ewm.exception;

import org.springframework.dao.DataAccessException;

public class ConflictException extends DataAccessException {
    public ConflictException(String msg) {
        super(msg);
    }
}
