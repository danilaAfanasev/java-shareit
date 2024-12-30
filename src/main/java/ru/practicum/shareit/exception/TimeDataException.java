package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeDataException extends BadRequestException {
    public TimeDataException(String message) {
        super(message);
    }
}
