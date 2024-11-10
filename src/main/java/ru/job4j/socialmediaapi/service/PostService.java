package ru.job4j.socialmediaapi.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.dto.PostDto;
import ru.job4j.socialmediaapi.dto.UserPostsDto;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Optional<PostDto> findById(int id);

    @Transactional(propagation = Propagation.REQUIRED)
    PostDto createPost(PostDto postDto);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean deletePostById(int id);

    @Transactional(propagation = Propagation.REQUIRED)
    void updateTitleAndDescription(String title, String description, int id);

    List<UserPostsDto> getUserPostsDtosByUserIds(List<Integer> userIds);
}
