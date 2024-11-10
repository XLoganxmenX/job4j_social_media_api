package ru.job4j.socialmediaapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimplePostDto {
    private int postId;
    private String title;
}
