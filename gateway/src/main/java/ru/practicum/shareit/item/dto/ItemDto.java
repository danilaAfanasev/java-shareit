package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.Marker;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;

    @Size(min = 1, max = 64, groups = {Marker.Create.class, Marker.Update.class})
    @NotBlank(groups = Marker.Create.class)
    private String name;

    @Size(min = 1, max = 64, groups = {Marker.Create.class, Marker.Update.class})
    @NotBlank(groups = Marker.Create.class)
    private String description;

    @NotNull(groups = Marker.Create.class)
    private Boolean available;
    private Long requestId;
}