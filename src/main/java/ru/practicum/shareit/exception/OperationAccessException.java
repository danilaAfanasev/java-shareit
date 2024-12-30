package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OperationAccessException extends RuntimeException {
    public OperationAccessException(String message) {
        super(message);
    }
}