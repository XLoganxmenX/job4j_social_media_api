package ru.job4j.socialmediaapi.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.dto.UserListDto;
import ru.job4j.socialmediaapi.enums.RelationTypeName;
import ru.job4j.socialmediaapi.mappers.UserMapper;
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

    private final UserMapper userMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void sendRequestToFriendships(int userId, int relatedUserId)
                                                                throws DataAccessException, NoSuchElementException {
        RelationType subscriberType = getRelationType(RelationTypeName.SUBSCRIBER);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        User relatedUser = userRepository.findById(relatedUserId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + relatedUserId + " not found"));
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
    public List<UserListDto> getSubscribersForUser(int userId)
                                                            throws DataAccessException, NoSuchElementException {
        RelationType subscriberType = getRelationType(RelationTypeName.SUBSCRIBER);
        RelationType friendType = getRelationType(RelationTypeName.FRIEND);
        return userRepository.
                findUsersByRelationTypeForRelatedUserWithoutType(userId, subscriberType.getId(), friendType.getId())
                .stream()
                .map(userMapper::getUserListDtoFromUser)
                .toList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void confirmFriendships(int userId, int friendUserId)
                                                            throws DataAccessException, NoSuchElementException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        User friendUser = userRepository.findById(friendUserId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + friendUserId + " not found"));
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
