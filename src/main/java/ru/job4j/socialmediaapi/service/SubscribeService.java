package ru.job4j.socialmediaapi.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.dto.UserListDto;

import java.util.List;

public interface SubscribeService {
    @Transactional(propagation = Propagation.REQUIRED)
    void sendRequestToFriendships(int userId, int relatedUserId);

    @Transactional(propagation = Propagation.REQUIRED)
    List<UserListDto> getSubscribersForUser(int userId);

    @Transactional(propagation = Propagation.REQUIRED)
    void confirmFriendships(int userId, int friendUserId);
}
