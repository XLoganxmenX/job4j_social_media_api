package ru.job4j.socialmediaapi.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.dto.PostPhotoDto;
import ru.job4j.socialmediaapi.model.Post;
import ru.job4j.socialmediaapi.model.User;

import java.util.List;

public interface PostService {

    @Transactional(propagation = Propagation.REQUIRED)
    Post createPost(String title, String description, User user, List<PostPhotoDto> postPhotos);

    @Transactional(propagation = Propagation.REQUIRED)
    void deletePostById(int id);

    @Transactional(propagation = Propagation.REQUIRED)
    void updateTitleAndDescription(String title, String description, int id);
}
