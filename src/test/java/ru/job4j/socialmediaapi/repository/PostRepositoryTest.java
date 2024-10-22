package ru.job4j.socialmediaapi.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.model.Post;
import ru.job4j.socialmediaapi.model.RelationType;
import ru.job4j.socialmediaapi.model.User;
import ru.job4j.socialmediaapi.model.UserRelate;

import java.time.LocalDateTime;
import java.util.HashSet;
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

    @Autowired
    private RelationTypeRepository relationTypeRepository;

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
    @Transactional
    public void whenSavePostAndDeleteThenNotFound() {
        var user = userRepository.save(
                new User(0, "user1", "test1@test.com", "test1", "UTC", Set.of()));
        var post1 = new Post(0, "title", "description", user, LocalDateTime.now(), List.of());
        var post2 = new Post(0, "title2", "description2", user, LocalDateTime.now(), List.of());
        postRepository.save(post1);
        int deletedRows = postRepository.deletePostById(post1.getId());
        var actualPost = postRepository.findById(post1.getId());
        assertThat(deletedRows).isEqualTo(1);
        assertThat(actualPost).isEmpty();
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
                .ignoringFields("user", "created", "postPhotos")
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

    @Test
    public void whenFindPostByUserThenGetUsersPostList() {
        var user = userRepository.save(
                new User(0, "user1", "test1@test.com", "test1", "UTC", Set.of()));
        var userPost1 = postRepository.save(
                new Post(0, "title1", "description1", user, LocalDateTime.now(), List.of()));
        var userPost2 = postRepository.save(
                new Post(0, "title2", "description2", user, LocalDateTime.now(), List.of()));
        var expectedPosts = List.of(userPost1, userPost2);

        var anotherUser = userRepository.save(
                new User(0, "another user", "anothertest@test.com", "another test", "UTC", Set.of()));
        var anotherUserPost = postRepository.save(
                new Post(0, "another title", "another description", anotherUser, LocalDateTime.now(), List.of()));

        var actualUserPosts = postRepository.findByUser(user);
        assertThat(actualUserPosts).hasSize(2);
        assertThat(actualUserPosts).usingRecursiveComparison()
                .ignoringFields("user", "created", "postPhotos")
                .isEqualTo(expectedPosts);
    }

    @Test
    public void whenFindPostByCreatedBetweenThenGetMonthPostList() {
        var today = LocalDateTime.now();
        var user = userRepository.save(
                new User(0, "user1", "test1@test.com", "test1", "UTC", Set.of()));
        var userMatchingPost1 = postRepository.save(
                new Post(0, "title1", "description1", user, today.minusDays(5), List.of()));
        var userMatchingPost2 = postRepository.save(
                new Post(0, "title2", "description2", user, today.minusDays(29), List.of()));
        var userNotMatchingPost3 = postRepository.save(
                new Post(0, "title3", "description3", user, today.minusMonths(6), List.of()));

        var anotherUser = userRepository.save(
                new User(0, "another user", "anothertest@test.com", "another test", "UTC", Set.of()));
        var anotherUserMatchingPost = postRepository.save(
                new Post(0, "another title", "another description", anotherUser, today.minusHours(1), List.of()));
        var expectedPosts = List.of(userMatchingPost1, userMatchingPost2, anotherUserMatchingPost);

        var actualUserPosts = postRepository.findByCreatedBetween(today.minusDays(30), today);
        assertThat(actualUserPosts).hasSize(3);
        assertThat(actualUserPosts).usingRecursiveComparison()
                .ignoringFields("user", "created", "postPhotos")
                .isEqualTo(expectedPosts);
    }

    @Test
    public void whenFindPostOrderByCreatedDescThenGetDescOrderPostList() {
        var today = LocalDateTime.now();
        var user = userRepository.save(
                new User(0, "user1", "test1@test.com", "test1", "UTC", Set.of()));
        var userPost1 = postRepository.save(
                new Post(0, "title1", "description1", user, today.minusDays(5), List.of()));
        var userPost2 = postRepository.save(
                new Post(0, "title2", "description2", user, today.minusHours(2), List.of()));
        var userPost3 = postRepository.save(
                new Post(0, "title3", "description3", user, today.minusMonths(6), List.of()));
        var expectedPosts = List.of(userPost2, userPost1, userPost3);

        var actualUserPosts = postRepository.findByOrderByCreatedDesc(PageRequest.of(0, 3)).getContent();
        assertThat(actualUserPosts).usingRecursiveComparison()
                .ignoringFields("user", "created", "postPhotos")
                .isEqualTo(expectedPosts);
        assertThat(actualUserPosts).containsExactlyElementsOf(expectedPosts);
    }

    @Test
    @Transactional
    public void whenUpdateTitleAndDescThenPostUpdated() {
        var user = userRepository.save(
                new User(0, "user1", "test1@test.com", "test1", "UTC", Set.of()));
        var post1 = postRepository.save(
                new Post(0, "title1", "description1", user, LocalDateTime.now(), List.of()));
        var post2 = postRepository.save(
                new Post(0, "title2", "description2", user, LocalDateTime.now(), List.of()));

        var updatedRows = postRepository.updateTitleAndDescription("new title", "new description", post1.getId());
        var updatedPost = postRepository.findById(post1.getId());

        assertThat(updatedRows).isEqualTo(1);
        assertThat(updatedPost).isPresent();
        assertThat(updatedPost.get().getTitle()).isEqualTo("new title");
        assertThat(updatedPost.get().getDescription()).isEqualTo("new description");
    }

    @Test
    @Transactional
    public void whenFindUsersByRelationTypeForUser() {
        var today = LocalDateTime.now();
        var user = userRepository.save(
                new User(0, "user1", "test1@test.com", "test1", "UTC", new HashSet<>()));
        var userPost = postRepository.save(
                new Post(0, "title1", "description1", user, today, List.of()));
        var relateUser2 = userRepository.save(
                new User(0, "user2", "tes2t@test.com", "test2", "UTC", Set.of()));
        var relateUser2Post = postRepository.save(
                new Post(0, "relateUser2 title", "relateUser2 description", relateUser2, today.minusDays(10), List.of()));
        var relateUser3 = userRepository.save(
                new User(0, "user3", "tes3t@test.com", "test3", "UTC", Set.of()));
        var relateUser3Post = postRepository.save(
                new Post(0, "relateUser3 title", "relateUser3 description", relateUser3, today.minusHours(1), List.of()));
        var subscriberType = relationTypeRepository.save(new RelationType(0, "subscriber"));
        user.getUserRelates()
                .add(new UserRelate(0, user, relateUser2, subscriberType));
        userRepository.save(user);
        user.getUserRelates()
                .add(new UserRelate(0, user, relateUser3, subscriberType));
        userRepository.save(user);

        var actualPosts = postRepository.findAllPostsByRelationType(
                user.getId(), subscriberType.getId(), PageRequest.of(0, 2)).getContent();
        assertThat(actualPosts).hasSize(2);
        assertThat(actualPosts).containsExactly(relateUser3Post, relateUser2Post);
        relationTypeRepository.deleteAll();
    }
}