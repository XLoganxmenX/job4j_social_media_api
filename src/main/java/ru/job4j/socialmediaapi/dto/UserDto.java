package ru.job4j.socialmediaapi.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotNull
    @PositiveOrZero(message = "Поле Id должно быть больше или равно 0")
    private int id;

    @NotBlank(message = "Поле name не должно быть пустым")
    private String name;

    @NotBlank(message = "Поле email не должно быть пустым")
    @Email
    private String email;

    @NotBlank(message = "Поле password не должно быть пустым")
    private String password;
}
