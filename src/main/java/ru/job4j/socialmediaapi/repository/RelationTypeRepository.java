package ru.job4j.socialmediaapi.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.job4j.socialmediaapi.model.RelationType;

public interface RelationTypeRepository extends ListCrudRepository<RelationType, Integer> {
}
