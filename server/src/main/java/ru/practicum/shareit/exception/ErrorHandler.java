package ru.practicum.shareit.exception;

import jakarta.validation.ValidationException;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Builder
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e) {
        log.warn("400 {}", e.getMessage(), e);
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotAvailableException(NotAvailableException e) {
        log.warn("400 {}", e.getMessage());
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateEmail(final ConstraintViolationException e) {
        log.warn("409 {}", e.getMessage());
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUnknownDataException(NotFoundException e) {
        log.warn("404 {}", e.getMessage());
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerAccessException(final OperationAccessException e) {
        log.warn("404 {}", e.getMessage());
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Exception e) {
        log.warn("500 {}", e.getMessage());
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnknownDataException(MethodArgumentNotValidException e) {
        log.warn("400 {}", e.getMessage());
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnknownDataException(TimeDataException e) {
        log.warn("400 {}", e.getMessage());
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable throwable) {
        log.error("Unknown error", throwable);
        return ErrorResponse.builder().error(throwable.getMessage()).build();
    }
}