package ru.job4j.socialmediaapi.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.socialmediaapi.model.RelationType;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RelationTypeRepositoryTest {
    @Autowired
    private RelationTypeRepository relationTypeRepository;

    @AfterEach
    public void setUp() {
        relationTypeRepository.deleteAll();
    }

    @Test
    public void whenSaveRelationTypeThenFindById() {
        var relationType = new RelationType(0, "friend");
        relationTypeRepository.save(relationType);
        var actualRelationType = relationTypeRepository.findById(relationType.getId());
        assertThat(actualRelationType).isPresent();
        assertThat(actualRelationType.get().getName()).isEqualTo(relationType.getName());
    }

    @Test
    public void whenFindByIdNotExistRelationTypeThenGetEmptyOptional() {
        var actualRelationType = relationTypeRepository.findById(0);
        assertThat(actualRelationType).isEmpty();
    }

    @Test
    public void whenSaveRelationTypeAndDeleteThenNotFound() {
        var relationType = new RelationType(0, "friend");
        relationTypeRepository.save(relationType);
        relationTypeRepository.deleteById(relationType.getId());
        var actualRelationType = relationTypeRepository.findById(relationType.getId());
        assertThat(actualRelationType).isEmpty();
        assertThat(relationTypeRepository.findAll()).isEmpty();
    }

    @Test
    public void whenFindAllRelationTypesThenGetTypesList() {
        var friendType = relationTypeRepository.save(new RelationType(0, "friend"));
        var subscriberType = relationTypeRepository.save(new RelationType(0, "subscriber"));
        var expectedTypes = List.of(friendType, subscriberType);
        var actualTypes = relationTypeRepository.findAll();
        assertThat(actualTypes).hasSize(2);
        assertThat(actualTypes).usingRecursiveComparison().isEqualTo(expectedTypes);
    }

    @Test
    public void whenUpdateRelationTypeAndSaveThenFindById() {
        var relationType = relationTypeRepository.save(new RelationType(0, "friend"));
        relationType.setName("subscriber");
        relationTypeRepository.save(relationType);
        var actualRelationType = relationTypeRepository.findById(relationType.getId());
        assertThat(actualRelationType).isPresent();
        assertThat(relationTypeRepository.findAll()).hasSize(1);
        assertThat(actualRelationType.get().getName()).isEqualTo("subscriber");
    }

    @Test
    public void whenFindAllRelationTypesNotExistThenGetEmptyList() {
        var actualRelationTypes = relationTypeRepository.findAll();
        assertThat(actualRelationTypes).isEmpty();
    }
}