package ru.job4j.socialmediaapi.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.job4j.socialmediaapi.dto.PostPhotoDto;
import ru.job4j.socialmediaapi.model.Post;
import ru.job4j.socialmediaapi.model.PostPhoto;
import ru.job4j.socialmediaapi.model.User;
import ru.job4j.socialmediaapi.repository.PostRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

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

    @BeforeAll
    public static void setUp() {
        postRepository = mock(PostRepository.class);
        postPhotoService = mock(PostPhotoServiceImpl.class);
        postService = new PostServiceImpl(postRepository, postPhotoService);
    }

    @Test
    public void whenCreatePostThenGetNewPost() {
        var postPhotoDtos = List.of(
                new PostPhotoDto("photo1", new byte[10])
        );
        var postPhotos = List.of(new PostPhoto(1, "photo1", "path"));
        var post = new Post(0, "title", "description",  new User(),
                LocalDateTime.now(ZoneId.of("UTC")), postPhotos);
        when(postPhotoService.saveAll(postPhotoDtos)).thenReturn(postPhotos);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        var createdPost = postService.createPost(post.getTitle(), post.getDescription(), post.getUser(), postPhotoDtos);
        assertThat(createdPost).usingRecursiveComparison().isEqualTo(post);
    }

    @Test
    public void whenCreatePostAndGetExceptionThenThrowException() {
        var postPhotoDtos = List.of(
                new PostPhotoDto("photo1", new byte[10])
        );
        var postPhotos = List.of(new PostPhoto(1, "photo1", "path"));
        var post = new Post(0, "title", "description", new User(),
                LocalDateTime.now(ZoneId.of("UTC")), postPhotos);
        when(postPhotoService.saveAll(postPhotoDtos)).thenReturn(postPhotos);
        when(postRepository.save(post)).thenThrow(DataIntegrityViolationException.class);
        assertThatThrownBy(() ->
                postService.createPost(post.getTitle(), post.getDescription(), post.getUser(), postPhotoDtos)
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