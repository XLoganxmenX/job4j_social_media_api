package ru.job4j.socialmediaapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostPhotoDto {
    @NotBlank(message = "Поле name не должно быть пустым")
    private String name;

    @NotBlank(message = "Поле base64Content не должно быть пустым")
    private String base64Content;
}
