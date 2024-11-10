package ru.job4j.socialmediaapi.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends ListCrudRepository<User, Integer> {
    @Modifying
    @Transactional
    @Query("delete from User u where u.id = :userId")
    int delete(@Param("userId") int userId);

    @Query("""
            select user from User as user
            where user.email = :email and user.password = :password
            """)
    Optional<User> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    @Query("""
            select ur.relatedUser from User u
            join u.userRelates ur
            join ur.relationType rt
            where u.id = :userId and rt.id = :relationTypeId
        """)
    List<User> findRelatedUsersByRelationTypeForUser(@Param("userId") int userId,
                                                     @Param("relationTypeId") int relationTypeId);

    @Query("""
            select ur.user from User u
            join u.userRelates ur
            join ur.relationType rt
            where ur.relatedUser.id = :relatedUserId and rt.id = :relationTypeId
            and not exists (
                    select 1 from UserRelate ur2
                    where ur2.user.id = ur.user.id
                      and ur2.relationType.id = :withoutTypeId)
        """)
    List<User> findUsersByRelationTypeForRelatedUserWithoutType(@Param("relatedUserId") int userId,
                                                                 @Param("relationTypeId") int relationTypeId,
                                                                 @Param("withoutTypeId") int withoutTypeId);
    @Query("""
            select user from User as user
            where user.id IN (:userIds)
            """)
    List<User> findUsersByIds(@Param("userIds") List<Integer> userIds);
}
