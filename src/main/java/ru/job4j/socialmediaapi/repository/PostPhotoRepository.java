package ru.job4j.socialmediaapi.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.model.PostPhoto;

public interface PostPhotoRepository extends ListCrudRepository<PostPhoto, Integer> {

    @Transactional(propagation = Propagation.REQUIRED)
    @Modifying(clearAutomatically = true)
    @Query("""
           delete from PostPhoto f
           where f.id = :postPhotoId
           """)
    int deletePostPhotoById(@Param("postPhotoId") int postPhotoId);
}
