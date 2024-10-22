package ru.job4j.socialmediaapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_photos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PostPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    private String name;

    private String path;
}
