package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
public class ItemRequestDto {
    private String description;
    private String requesterName;
    private LocalDateTime created;
}
