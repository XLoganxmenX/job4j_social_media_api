package ru.job4j.socialmediaapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "relation_types")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RelationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    private String name;
}
