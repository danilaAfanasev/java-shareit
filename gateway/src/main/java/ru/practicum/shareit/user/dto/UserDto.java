package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class UserDto {
    private Long id;

    @NotBlank(groups = Marker.Create.class)
    @Size(min = 1, max = 64, groups = {Marker.Create.class, Marker.Update.class})
    private String name;

    @NotBlank(groups = Marker.Create.class)
    @Email(groups = {Marker.Create.class, Marker.Update.class})
    @Size(min = 1, max = 64, groups = {Marker.Create.class, Marker.Update.class})
    private String email;
}