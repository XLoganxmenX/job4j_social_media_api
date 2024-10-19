package ru.job4j.socialmediaapi.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.socialmediaapi.model.Post;
import ru.job4j.socialmediaapi.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void whenSavePostThenFindById() {
        var user = userRepository.save(
                new User(0, "user1", "test1@test.com", "test1", "UTC", Set.of()));
        var post = new Post(0, "title", "description", user, LocalDateTime.now(), List.of());
        postRepository.save(post);
        var actualPost = postRepository.findById(post.getId());
        assertThat(actualPost).isPresent();
        assertThat(actualPost.get().getTitle()).isEqualTo(post.getTitle());
    }

    @Test
    public void whenFindByIdNotExistPostThenGetEmptyOptional() {
        var actualPost = postRepository.findById(0);
        assertThat(actualPost).isEmpty();
    }

    @Test
    public void whenSavePostAndDeleteThenNotFound() {
        var user = userRepository.save(
                new User(0, "user1", "test1@test.com", "test1", "UTC", Set.of()));
        var post = new Post(0, "title", "description", user, LocalDateTime.now(), List.of());
        postRepository.save(post);
        postRepository.deleteById(post.getId());
        var actualPost = postRepository.findById(post.getId());
        assertThat(actualPost).isEmpty();
        assertThat(postRepository.findAll()).isEmpty();
    }

    @Test
    public void whenFindAllPostThenGetPostsList() {
        var user = userRepository.save(
                new User(0, "user1", "test1@test.com", "test1", "UTC", Set.of()));
        var post1 = postRepository.save(
                new Post(0, "title1", "description1", user, LocalDateTime.now(), List.of()));
        var post2 = postRepository.save(
                new Post(0, "title2", "description2", user, LocalDateTime.now(), List.of()));
        var expectedPosts = List.of(post1, post2);
        var actualPosts = postRepository.findAll();
        assertThat(actualPosts).hasSize(2);
        assertThat(actualPosts).usingRecursiveComparison()
                .ignoringFields("user", "created", "files")
                .isEqualTo(expectedPosts);
    }

    @Test
    public void whenUpdatePostAndSaveThenFindById() {
        var user = userRepository.save(
                new User(0, "user1", "test1@test.com", "test1", "UTC", Set.of()));
        var post = postRepository.save(
                new Post(0, "title1", "description1", user, LocalDateTime.now(), List.of()));
        post.setTitle("new title");
        post.setDescription("new description");
        postRepository.save(post);
        var actualPost = postRepository.findById(post.getId());
        assertThat(actualPost).isPresent();
        assertThat(postRepository.findAll()).hasSize(1);
        assertThat(actualPost.get().getTitle()).isEqualTo("new title");
        assertThat(actualPost.get().getDescription()).isEqualTo("new description");
    }

    @Test
    public void whenFindAllPostsNotExistThenGetEmptyList() {
        var actualPosts = postRepository.findAll();
        assertThat(actualPosts).isEmpty();
    }
}