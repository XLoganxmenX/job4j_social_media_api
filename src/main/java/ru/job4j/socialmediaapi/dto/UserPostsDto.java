package ru.job4j.socialmediaapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPostsDto {
    private int userId;
    private String username;
    private List<SimplePostDto> userPosts;
}
