package ru.job4j.socialmediaapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import ru.job4j.socialmediaapi.model.User;
import ru.job4j.socialmediaapi.service.PostService;

import java.util.List;

@Tag(name = "PostController", description = "API управления постами")
@RestController
@RequestMapping("/social-media/post")
@Validated
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @Operation(
            summary = "Получить пост по id",
            description = """
                        Получить объект PostDto по его id.
                        Ответом возвращается объект PostDto с полями id, title, description, userId,
                        userName и списком объектов PostPhotos
                        """,
            tags = { "Post", "find" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = User.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {
                    @Content(schema = @Schema())})
    })
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> get(@PathVariable("postId")
                                       @NotNull
                                       @Min(value = 1, message = "Номер ресурса должен быть больше 0")
                                       int postId) {
        return postService.findById(postId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Сохранить пост",
            description = """
                        Сохранить пост, отправив заполненный объект PostDto с 0 id.
                        Ответом возвращается объект PostDto с полями id, title, description, userId,
                        userName и списком объектов PostPhotos.
                        """,
            tags = { "Post", "save" })
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(schema = @Schema(implementation = User.class), mediaType = "application/json")}),
    })
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

    @Operation(
            summary = "Удалить поста",
            description = """
                        Удаление поста по его id.
                        """,
            tags = { "Post", "delete" })
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
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

    @Operation(
            summary = "Получить список постов определенных пользователей",
            description = """
                        Получить список объектов UserPostsDto по списку их Id.
                        Ответом возвращается объект UserPostsDto с полями userId, userName
                        и списком объектов SimpleUserPost.
                        """,
            tags = { "Post", "User", "find" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserPostsDto.class)))}),
    })
    @PostMapping("/by-user-ids")
    @ResponseStatus(HttpStatus.OK)
    public List<UserPostsDto> getUsersWithPosts(@NotEmpty @RequestBody List<Integer> userIds) {
        return postService.getUserPostsDtosByUserIds(userIds);
    }
}
