package ru.job4j.socialmediaapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.job4j.socialmediaapi.model.Post;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private int id;
    private String title;
    private String description;
    private int userId;
    private String userName;
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
