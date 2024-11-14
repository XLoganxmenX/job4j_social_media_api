package ru.job4j.socialmediaapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "UserDto Information")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotNull
    @PositiveOrZero(message = "Поле Id должно быть больше или равно 0")
    private int id;

    @Schema(description = "ФИО пользователя", example = "Иванов Иван Иванович")
    @NotBlank(message = "Поле name не должно быть пустым")
    private String name;

    @Schema(description = "Email пользователя", example = "Ivanov@test.ru")
    @NotBlank(message = "Поле email не должно быть пустым")
    @Email
    private String email;

    @Schema(description = "Пароль пользователя", example = "ivanov_yghsafjg1341")
    @NotBlank(message = "Поле password не должно быть пустым")
    private String password;
}
