package ru.job4j.socialmediaapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.job4j.socialmediaapi.model.Post;

import java.util.List;

@Schema(description = "PostDto Information")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    @NotNull
    @PositiveOrZero(message = "Поле Id должно быть больше или равно 0")
    private int id;

    @Schema(description = "Заголовок поста", example = "Мой новый тестовый пост")
    @NotBlank(message = "Поле title не должно быть пустым")
    private String title;

    @Schema(description = "Основной текст поста", example = "Мой основной тестовый текст")
    @NotBlank(message = "Поле description не должно быть пустым")
    private String description;

    @Schema(description = "Id пользователя, создавшего пост", example = "1")
    @NotNull
    @Positive(message = "Поле userId должно быть больше 0")
    private int userId;

    @Schema(description = "Имя пользователя, создавшего пост", example = "Иванов Иван Иванович")
    @NotBlank(message = "Поле userName не должно быть пустым")
    private String userName;

    @Schema(description = "Список объектов-фото, прикрепленных к посту")
    @NotNull
    private List<PostPhotoDto> postPhotos;

    public PostDto(Post post, List<PostPhotoDto> postPhotos) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.description = post.getDescription();
        this.userId = post.getUser().getId();
        this.userName = post.getUser().getName();
        this.postPhotos = postPhotos;
    }
}
