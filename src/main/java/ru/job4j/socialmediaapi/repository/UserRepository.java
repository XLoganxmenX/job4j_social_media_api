package ru.job4j.socialmediaapi.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import ru.job4j.socialmediaapi.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends ListCrudRepository<User, Integer> {
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
    List<User> findUsersByRelationTypeForUser(@Param("userId") int userId,
                                              @Param("relationTypeId") int relationTypeId);
}
