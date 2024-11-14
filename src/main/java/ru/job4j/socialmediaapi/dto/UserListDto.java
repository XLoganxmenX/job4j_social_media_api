package ru.job4j.socialmediaapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "UserListDto Information")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListDto {
    @Schema(description = "Id пользователя", example = "1")
    private int id;

    @Schema(description = "ФИО пользователя", example = "Иванов Иван Иванович")
    private String name;
}
