package ru.job4j.socialmediaapi.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.enums.RelationTypeName;
import ru.job4j.socialmediaapi.model.RelationType;
import ru.job4j.socialmediaapi.model.User;
import ru.job4j.socialmediaapi.model.UserRelate;
import ru.job4j.socialmediaapi.repository.RelationTypeRepository;
import ru.job4j.socialmediaapi.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class SubscribeServiceImpl implements SubscribeService {
    private final RelationTypeRepository relationTypeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void sendRequestToFriendships(User user, int relatedUserId) throws DataAccessException {
        RelationType subscriberType = getRelationType(RelationTypeName.SUBSCRIBER);
        User relatedUser = userRepository.findById(relatedUserId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + " not found"));
        UserRelate newSubscriberRelate = new UserRelate(0, user, relatedUser, subscriberType);
        user.getUserRelates().add(newSubscriberRelate);
        userRepository.save(user);
    }

    private RelationType getRelationType(RelationTypeName relationTypeName) {
        return relationTypeRepository.findByName(relationTypeName.name())
                .orElseThrow(() -> new NoSuchElementException(
                        "Relation type with name " + relationTypeName.name() + " not found"));
    }

    @Override
    public List<User> getSubscribersForUser(int userId) throws DataAccessException {
        RelationType subscriberType = getRelationType(RelationTypeName.SUBSCRIBER);
        RelationType friendType = getRelationType(RelationTypeName.FRIEND);
        return userRepository.
                findUsersByRelationTypeForRelatedUserWithoutType(userId, subscriberType.getId(), friendType.getId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void confirmFriendships(User user, User friendUser) throws DataAccessException {
        RelationType subscriberType = getRelationType(RelationTypeName.SUBSCRIBER);
        RelationType friendType = getRelationType(RelationTypeName.FRIEND);
        user.getUserRelates().addAll(
                List.of(
                        new UserRelate(0, user, friendUser, subscriberType),
                        new UserRelate(0, user, friendUser, friendType)
                )
        );
        userRepository.save(user);
        friendUser.getUserRelates().add(
                new UserRelate(0, friendUser, user, friendType)
        );
        userRepository.save(friendUser);
    }
}
