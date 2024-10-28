package ru.job4j.socialmediaapi.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.job4j.socialmediaapi.model.RelationType;

import java.util.Optional;

public interface RelationTypeRepository extends ListCrudRepository<RelationType, Integer> {
    Optional<RelationType> findByName(String name);
}
