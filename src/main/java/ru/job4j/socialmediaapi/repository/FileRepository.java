package ru.job4j.socialmediaapi.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import ru.job4j.socialmediaapi.model.File;

public interface FileRepository extends ListCrudRepository<File, Integer> {


    @Modifying(clearAutomatically = true)
    @Query("""
           delete from File f
           where f.id = :fileId
           """)
    int deleteFileById(@Param("fileId") int fileId);
}
