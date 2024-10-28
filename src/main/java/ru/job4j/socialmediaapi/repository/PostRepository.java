package ru.job4j.socialmediaapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import ru.job4j.socialmediaapi.model.Post;
import ru.job4j.socialmediaapi.model.User;


import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends ListCrudRepository<Post, Integer> {
    List<Post> findByUser(User user);

    List<Post> findByCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);

    Page<Post> findByOrderByCreatedDesc(Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("""
           update Post post
           set post.title = :title, post.description = :description
           where post.id = :id
           """)
    int updateTitleAndDescriptionById(@Param("title") String title,
                                      @Param("description") String description,
                                      @Param("id") int id);

    @Modifying(clearAutomatically = true)
    @Query("""
           delete from Post post where post.id = :id
           """)
    int deletePostById(@Param("id") int id);

    @Query("""
           select p from Post p
           join p.user u
           join UserRelate ur on ur.relatedUser.id = u.id
           join ur.relationType rt
           where ur.user.id = :userId and rt.id = :relationTypeId
           order by p.created desc
           """)
    Page<Post> findAllPostsByRelationType(@Param("userId") int userId,
                                          @Param("relationTypeId") int relationTypeId,
                                          Pageable pageable);
}
