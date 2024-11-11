package ru.job4j.socialmediaapi.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.job4j.socialmediaapi.dto.PostDto;
import ru.job4j.socialmediaapi.dto.UserPostsDto;
import ru.job4j.socialmediaapi.service.PostService;

import java.util.List;


@RestController
@RequestMapping("/social-media/post")
@Validated
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> get(@PathVariable("postId")
                                       @NotNull
                                       @Min(value = 1, message = "Номер ресурса должен быть больше 0")
                                       int postId) {
        return postService.findById(postId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping()
    public ResponseEntity<PostDto> save(@Valid @RequestBody PostDto postDto) {
        postDto = postService.createPost(postDto);
        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(postDto.getId())
                .toUri();
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(uri)
                .body(postDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id")
                                       @NotNull
                                       @Min(value = 1, message = "Номер ресурса должен быть больше 0")
                                       int id) {
        if (postService.deletePostById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/by-user-ids")
    @ResponseStatus(HttpStatus.OK)
    public List<UserPostsDto> getUsersWithPosts(@NotEmpty @RequestBody List<Integer> userIds) {
        return postService.getUserPostsDtosByUserIds(userIds);
    }
}
