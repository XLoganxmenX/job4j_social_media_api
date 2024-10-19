package ru.job4j.socialmediaapi.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.socialmediaapi.model.File;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileRepositoryTest {
    @Autowired
    private FileRepository fileRepository;

    @BeforeEach
    public void setUp() {
        fileRepository.deleteAll();
    }

    @Test
    public void whenSaveFileThenFindById() {
        var file = new File(0, "file", "path");
        fileRepository.save(file);
        var actualFile = fileRepository.findById(file.getId());
        assertThat(actualFile).isPresent();
        assertThat(actualFile.get().getName()).isEqualTo(file.getName());
    }

    @Test
    public void whenFindByIdNotExistFileThenGetEmptyOptional() {
        var actualFile = fileRepository.findById(0);
        assertThat(actualFile).isEmpty();
    }

    @Test
    public void whenSaveFileAndDeleteThenNotFound() {
        var file = new File(0, "file", "path");
        fileRepository.save(file);
        fileRepository.deleteById(file.getId());
        var actualFile = fileRepository.findById(file.getId());
        assertThat(actualFile).isEmpty();
        assertThat(fileRepository.findAll()).isEmpty();
    }

    @Test
    public void whenFindAllFilesThenGetFilesList() {
        var file1 = fileRepository.save(new File(0, "file1", "path1"));
        var file2 = fileRepository.save(new File(0, "file2", "path2"));
        var expectedFiles = List.of(file1, file2);
        var actualFiles = fileRepository.findAll();
        assertThat(actualFiles).hasSize(2);
        assertThat(actualFiles).usingRecursiveComparison().isEqualTo(expectedFiles);
    }

    @Test
    public void whenUpdateFileAndSaveThenFindById() {
        var file = fileRepository.save(new File(0, "file", "path"));
        file.setName("updatedFile");
        file.setPath("updatedPath");
        fileRepository.save(file);
        var actualFile = fileRepository.findById(file.getId());
        assertThat(actualFile).isPresent();
        assertThat(fileRepository.findAll()).hasSize(1);
        assertThat(actualFile.get().getName()).isEqualTo("updatedFile");
        assertThat(actualFile.get().getPath()).isEqualTo("updatedPath");
    }

    @Test
    public void whenFindAllFilesNotExistThenGetEmptyList() {
        var actualFiles = fileRepository.findAll();
        assertThat(actualFiles).isEmpty();
    }
}