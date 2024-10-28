package ru.job4j.socialmediaapi.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.model.Post;
import ru.job4j.socialmediaapi.model.PostPhoto;
import ru.job4j.socialmediaapi.model.User;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostPhotoRepositoryTest {
    @Autowired
    private PostPhotoRepository postPhotoRepository;

    @BeforeEach
    public void setUp() {
        postPhotoRepository.deleteAll();
    }

    @Test
    public void whenSavePostPhotoThenFindById() {
        var postPhoto = new PostPhoto(0, "postPhoto", "path");
        postPhotoRepository.save(postPhoto);
        var actualPostPhoto = postPhotoRepository.findById(postPhoto.getId());
        assertThat(actualPostPhoto).isPresent();
        assertThat(actualPostPhoto.get().getName()).isEqualTo(postPhoto.getName());
    }

    @Test
    public void whenPostPhotoByIdNotExistPostPhotoThenGetEmptyOptional() {
        var actualPostPhoto = postPhotoRepository.findById(0);
        assertThat(actualPostPhoto).isEmpty();
    }

    @Test
    @Transactional
    public void whenSavePostPhotoAndDeleteThenNotFound() {
        var postPhoto = new PostPhoto(0, "postPhoto", "path");
        postPhotoRepository.save(postPhoto);
        var deletedRows = postPhotoRepository.deletePostPhotoById(postPhoto.getId());
        var actualPOstPhoto = postPhotoRepository.findById(postPhoto.getId());
        assertThat(deletedRows).isEqualTo(1);
        assertThat(actualPOstPhoto).isEmpty();
        assertThat(postPhotoRepository.findAll()).isEmpty();
    }

    @Test
    public void whenFindAllPostPhotosThenGetPostPhotosList() {
        var postPhoto1 = postPhotoRepository.save(new PostPhoto(0, "postPhoto1", "path1"));
        var postPhoto2 = postPhotoRepository.save(new PostPhoto(0, "postPhoto2", "path2"));
        var expectedPostPhotos = List.of(postPhoto1, postPhoto2);
        var actualPostPhotos = postPhotoRepository.findAll();
        assertThat(actualPostPhotos).hasSize(2);
        assertThat(actualPostPhotos).usingRecursiveComparison().isEqualTo(expectedPostPhotos);
    }

    @Test
    public void whenUpdatePostPhotoAndSaveThenFindById() {
        var postPhoto = postPhotoRepository.save(new PostPhoto(0, "postPhoto", "path"));
        postPhoto.setName("updatedPostPhoto");
        postPhoto.setPath("updatedPath");
        postPhotoRepository.save(postPhoto);
        var actualPostPhoto = postPhotoRepository.findById(postPhoto.getId());
        assertThat(actualPostPhoto).isPresent();
        assertThat(postPhotoRepository.findAll()).hasSize(1);
        assertThat(actualPostPhoto.get().getName()).isEqualTo("updatedPostPhoto");
        assertThat(actualPostPhoto.get().getPath()).isEqualTo("updatedPath");
    }

    @Test
    public void whenFindAllPostPhotosNotExistThenGetEmptyList() {
        var postPhotos = postPhotoRepository.findAll();
        assertThat(postPhotos).isEmpty();
    }
}