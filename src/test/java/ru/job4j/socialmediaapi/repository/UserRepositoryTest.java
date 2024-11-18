package ru.job4j.socialmediaapi.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.model.RelationType;
import ru.job4j.socialmediaapi.model.User;
import ru.job4j.socialmediaapi.model.UserRelate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RelationTypeRepository relationTypeRepository;

    @AfterEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void whenSaveUserThenFindById() {
        var user = new User(0, "user", "test@test.com", "test", "UTC", List.of(), Set.of());
        userRepository.save(user);
        var actualUser = userRepository.findById(user.getId());
        assertThat(actualUser).isPresent();
        assertThat(actualUser.get().getName()).isEqualTo(user.getName());
    }

    @Test
    public void whenFindByIdNotExistUserThenGetEmptyOptional() {
        var actualUser = userRepository.findById(0);
        assertThat(actualUser).isEmpty();
    }

    @Test
    public void whenSaveUserAndDeleteThenNotFound() {
        var user = new User(0, "user", "test@test.com", "test", "UTC", List.of(), Set.of());
        userRepository.save(user);
        userRepository.deleteById(user.getId());
        var actualUser = userRepository.findById(user.getId());
        assertThat(actualUser).isEmpty();
        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    public void whenFindAllUsersThenGetUsersList() {
        var user1 = userRepository.save(
                new User(0, "user1", "test1@test.com", "test1", "UTC", List.of(), Set.of()));
        var user2 = userRepository.save(
                new User(0, "user2", "test2@test.com", "test2", "UTC", List.of(), Set.of()));
        var expectedUsers = List.of(user1, user2);
        var actualUsers = userRepository.findAll();
        assertThat(actualUsers).hasSize(2);
        assertThat(actualUsers).usingRecursiveComparison()
                .ignoringFields("userRelates")
                .ignoringFields("roles")
                .isEqualTo(expectedUsers);
    }

    @Test
    public void whenFindAllUsersNotExistThenGetEmptyList() {
        var actualUsers = userRepository.findAll();
        assertThat(actualUsers).isEmpty();
    }

    @Test
    @Transactional
    public void whenSaveUserWithRelateThenFindRelate() {
        var user = userRepository.save(
                new User(0, "user1", "test1@test.com", "test1", "UTC", new ArrayList<>(), Set.of()));
        var relateUser = userRepository.save(
                new User(0, "user2", "tes2t@test.com", "test2", "UTC", List.of(), Set.of()));
        var friendType = relationTypeRepository.save(new RelationType(0, "friend"));
        user.getUserRelates().add(
                new UserRelate(0, user, relateUser, friendType)
        );
        userRepository.save(user);
        var actualUser = userRepository.findById(user.getId());
        assertThat(actualUser).isPresent();
        assertThat(actualUser.get().getUserRelates()).hasSize(1);
        assertThat(actualUser.get().getUserRelates()).isEqualTo(user.getUserRelates());
        relationTypeRepository.deleteAll();
    }

    @Test
    public void whenFindUserByEmailAndPasswordThenGetUser() {
        var user = userRepository.save(
                new User(0, "user", "test@test.com", "test", "UTC", new ArrayList<>(), Set.of()));
        var anotherUser = userRepository.save(
                new User(0, "another user", "anothertest@test.com",
                        "test", "UTC", new ArrayList<>(), Set.of()));
        var actualUser = userRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());
        assertThat(actualUser).isPresent();
        assertThat(actualUser.get()).usingRecursiveComparison()
                .ignoringFields("userRelates")
                .ignoringFields("roles")
                .isEqualTo(user);
    }

    @Test
    @Transactional
    public void whenFindRelatedUsersByRelationTypeForUser() {
        var user = userRepository.save(
                new User(0, "user", "test@test.com", "test", "UTC", new ArrayList<>(), Set.of()));
        var relateUser2 = userRepository.save(
                new User(0, "user2", "tes2t@test.com", "test2", "UTC", List.of(), Set.of()));
        var relateUser3 = userRepository.save(
                new User(0, "user3", "tes3t@test.com", "test3", "UTC", List.of(), Set.of()));
        var friendType = relationTypeRepository.save(new RelationType(0, "friend"));

        user.getUserRelates()
                .add(new UserRelate(0, user, relateUser2, friendType));
        userRepository.save(user);
        user.getUserRelates()
                .add(new UserRelate(0, user, relateUser3, friendType));
        userRepository.save(user);

        var userFriends = userRepository.findRelatedUsersByRelationTypeForUser(user.getId(), friendType.getId());
        assertThat(userFriends).hasSize(2);
        assertThat(userFriends).containsExactlyInAnyOrder(relateUser2, relateUser3);
        relationTypeRepository.deleteAll();
    }

    @Test
    @Transactional
    public void whenFindUsersByRelationTypeForRelatedUserWithoutType() {
        var relateUser = userRepository.save(
                new User(0, "relateUser", "test@test.com",
                        "test", "UTC", new ArrayList<>(), Set.of()));
        var user1 = userRepository.save(
                new User(0, "user2", "tes2t@test.com",
                        "test2", "UTC", new ArrayList<>(), Set.of()));
        var user2 = userRepository.save(
                new User(0, "user3", "tes3t@test.com",
                        "test3", "UTC", new ArrayList<>(), Set.of()));
        var subscriberType = relationTypeRepository.save(new RelationType(0, "subscriber"));
        var friendType = relationTypeRepository.save(new RelationType(0, "friend"));

        user1.getUserRelates().addAll(
                List.of(
                        new UserRelate(0, user1, relateUser, subscriberType),
                        new UserRelate(0, user1, relateUser, friendType)
                )
        );
        userRepository.save(user1);
        user2.getUserRelates()
                .add(new UserRelate(0, user2, relateUser, subscriberType));
        userRepository.save(user2);

        var subscribers = userRepository.findUsersByRelationTypeForRelatedUserWithoutType(
                relateUser.getId(), subscriberType.getId(), friendType.getId());
        assertThat(subscribers).hasSize(1);
        assertThat(subscribers).containsExactlyInAnyOrder(user2);
        relationTypeRepository.deleteAll();
    }
}