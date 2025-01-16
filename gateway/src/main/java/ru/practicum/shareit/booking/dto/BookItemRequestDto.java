package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.validate.StartBeforeEndDateValid;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndDateValid
public class BookItemRequestDto {
    private long itemId;

    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
}