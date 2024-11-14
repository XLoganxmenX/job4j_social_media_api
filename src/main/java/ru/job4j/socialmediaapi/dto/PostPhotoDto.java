package ru.job4j.socialmediaapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "PostPhotoDto Information")
@Data
@AllArgsConstructor
public class PostPhotoDto {
    @Schema(description = "Название изображения", example = "MyPicture")
    @NotBlank(message = "Поле name не должно быть пустым")
    private String name;

    @Schema(description = "Содержимое, закодированное в base64")
    @NotBlank(message = "Поле base64Content не должно быть пустым")
    private String base64Content;
}
