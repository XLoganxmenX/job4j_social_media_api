package ru.job4j.socialmediaapi.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.socialmediaapi.model.Message;
import ru.job4j.socialmediaapi.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageRepositoryTest {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void setUp() {
        messageRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void whenSaveMessageThenFindById() {
        var user1 = userRepository.save(
                new User(0, "user1", "test1@test.com", "test1", "UTC", List.of()));
        var user2 = userRepository.save(
                new User(0, "user2", "test2@test.com", "test2", "UTC", List.of()));
        var message = new Message(0, user1, user2, "message", LocalDateTime.now());
        messageRepository.save(message);
        var actualMessage = messageRepository.findById(message.getId());
        assertThat(actualMessage).isPresent();
        assertThat(actualMessage.get().getMessage()).isEqualTo(message.getMessage());
    }

    @Test
    public void whenFindByIdNotExistMessageThenGetEmptyOptional() {
        var actualMessage = messageRepository.findById(0);
        assertThat(actualMessage).isEmpty();
    }

    @Test
    public void whenSaveMessageAndDeleteThenNotFound() {
        var user1 = userRepository.save(
                new User(0, "user1", "test1@test.com", "test1", "UTC", List.of()));
        var user2 = userRepository.save(
                new User(0, "user2", "test2@test.com", "test2", "UTC", List.of()));
        var message = new Message(0, user1, user2, "message", LocalDateTime.now());
        messageRepository.save(message);
        messageRepository.deleteById(message.getId());
        var actualMessage = messageRepository.findById(message.getId());
        assertThat(actualMessage).isEmpty();
        assertThat(messageRepository.findAll()).isEmpty();
    }

    @Test
    public void whenFindAllMessagesThenGetMessagesList() {
        var user1 = userRepository.save(
                new User(0, "user1", "test1@test.com", "test1", "UTC", List.of()));
        var user2 = userRepository.save(
                new User(0, "user2", "test2@test.com", "test2", "UTC", List.of()));
        var message1 = messageRepository.save(new Message(0, user1, user2, "message1", LocalDateTime.now()));
        var message2 = messageRepository.save(new Message(0, user1, user2, "message2", LocalDateTime.now()));
        var expectedMessages = List.of(message1, message2);
        var actualMessages = messageRepository.findAll();
        assertThat(actualMessages).hasSize(2);
        assertThat(actualMessages).usingRecursiveComparison()
                .ignoringFields("fromUser", "toUser", "created")
                .isEqualTo(expectedMessages);
    }

    @Test
    public void whenUpdateMessageAndSaveThenFindById() {
        var user1 = userRepository.save(
                new User(0, "user1", "test1@test.com", "test1", "UTC", List.of()));
        var user2 = userRepository.save(
                new User(0, "user2", "test2@test.com", "test2", "UTC", List.of()));
        var message = messageRepository.save(new Message(0, user1, user2, "message", LocalDateTime.now()));
        message.setMessage("new message");
        messageRepository.save(message);
        var actualMessage = messageRepository.findById(message.getId());
        assertThat(actualMessage).isPresent();
        assertThat(messageRepository.findAll()).hasSize(1);
        assertThat(actualMessage.get().getMessage()).isEqualTo("new message");
    }

    @Test
    public void whenFindAllMessagesNotExistThenGetEmptyList() {
        var actualMessages = messageRepository.findAll();
        assertThat(actualMessages).isEmpty();
    }
}