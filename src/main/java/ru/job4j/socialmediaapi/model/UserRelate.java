package ru.job4j.socialmediaapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users_friendships")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserRelate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_user_id")
    private User relatedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relation_type_id")
    private RelationType relationType;
}
