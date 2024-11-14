package ru.job4j.socialmediaapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "SimplePostDto Information")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimplePostDto {
    @Schema(description = "Id поста", example = "1")
    private int postId;

    @Schema(description = "Заголовок поста", example = "Мой первый пост")
    private String title;
}
