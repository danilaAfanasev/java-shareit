package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
public class Item {
    private Long id;

    @NotBlank
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;
}
