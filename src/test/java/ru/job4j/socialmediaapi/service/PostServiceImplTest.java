package ru.job4j.socialmediaapi.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.job4j.socialmediaapi.dto.PostDto;
import ru.job4j.socialmediaapi.dto.PostPhotoDto;
import ru.job4j.socialmediaapi.model.Post;
import ru.job4j.socialmediaapi.model.PostPhoto;
import ru.job4j.socialmediaapi.model.User;
import ru.job4j.socialmediaapi.repository.PostRepository;
import ru.job4j.socialmediaapi.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostServiceImplTest {

    private static PostService postService;
    private static PostRepository postRepository;
    private static PostPhotoService postPhotoService;
    private static UserRepository userRepository;

    @BeforeAll
    public static void setUp() {
        postRepository = mock(PostRepository.class);
        postPhotoService = mock(PostPhotoServiceImpl.class);
        userRepository = mock(UserRepository.class);
        postService = new PostServiceImpl(postRepository, postPhotoService, userRepository);
    }

    @Test
    public void whenCreatePostThenGetNewPost() {
        var postPhotoDtos = List.of(new PostPhotoDto("photo1", new byte[10]));
        var postPhotos = List.of(new PostPhoto(1, "photo1", "path"));
        var user = new User(1, "user", "test@test.com", "test", "UTC", new ArrayList<>());
        var post = new Post(
                1,
                "title",
                "description",
                user,
                LocalDateTime.now(ZoneId.of("UTC")),
                postPhotos
        );
        var postDto = new PostDto(post, postPhotoDtos);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(postPhotoService.saveAll(postPhotoDtos)).thenReturn(postPhotos);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        var createdPostDto = postService.createPost(postDto);
        assertThat(createdPostDto.getId()).isEqualTo(post.getId());
        assertThat(createdPostDto.getTitle()).isEqualTo(post.getTitle());
        assertThat(createdPostDto.getDescription()).isEqualTo(post.getDescription());
        assertThat(createdPostDto.getPostPhotos()).usingRecursiveComparison().isEqualTo(postPhotoDtos);
    }

    @Test
    public void whenCreatePostAndGetExceptionThenThrowException() {
        var postPhotoDtos = List.of(new PostPhotoDto("photo1", new byte[10]));
        var postPhotos = List.of(new PostPhoto(1, "photo1", "path"));
        var user = new User(1, "user", "test@test.com", "test", "UTC", new ArrayList<>());
        var post = new Post(
                1,
                "title",
                "description",
                user,
                LocalDateTime.now(ZoneId.of("UTC")),
                postPhotos
        );
        var postDto = new PostDto(post, postPhotoDtos);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(postPhotoService.saveAll(postPhotoDtos)).thenReturn(postPhotos);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postService.createPost(postDto)).thenThrow(DataIntegrityViolationException.class);
        assertThatThrownBy(() ->
                postService.createPost(postDto)
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void whenDeletePostByIdAndGetExceptionThenThrowException() {
        when(postRepository.findById(0)).thenThrow(DataIntegrityViolationException.class);
        assertThatThrownBy(() ->
                postService.deletePostById(0)
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void whenUpdateTitleAndDescriptionAndGetExceptionThenThrowException() {
        when(postRepository.updateTitleAndDescriptionById("title", "description", 0))
                .thenThrow(DataIntegrityViolationException.class);
        assertThatThrownBy(() ->
                postService.updateTitleAndDescription("title", "description", 0)
        ).isInstanceOf(DataIntegrityViolationException.class);
    }
}