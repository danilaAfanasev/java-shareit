package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
/**
 * TODO Sprint add-controllers.
 */
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
public class User {
    private Long id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;
}
