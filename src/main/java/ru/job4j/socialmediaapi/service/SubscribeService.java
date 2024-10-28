package ru.job4j.socialmediaapi.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.model.User;

import java.util.List;

public interface SubscribeService {
    @Transactional(propagation = Propagation.REQUIRED)
    void sendRequestToFriendships(User user, int relatedUserId);

    @Transactional(propagation = Propagation.REQUIRED)
    List<User> getSubscribersForUser(int userId);

    @Transactional(propagation = Propagation.REQUIRED)
    void confirmFriendships(User user, User friendUser);
}
