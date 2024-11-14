package ru.job4j.socialmediaapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "UserPostsDto Information")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPostsDto {
    @Schema(description = "Id пользователя, создавшего пост", example = "1")
    private int userId;

    @Schema(description = "ФИО пользователя, создавшего пост", example = "Иванов Иван Иванович")
    private String username;

    @Schema(description = "Список объектов-постов пользователя")
    private List<SimplePostDto> userPosts;
}
