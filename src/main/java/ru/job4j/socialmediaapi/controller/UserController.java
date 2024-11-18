package ru.job4j.socialmediaapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.job4j.socialmediaapi.dto.UserDto;
import ru.job4j.socialmediaapi.model.User;
import ru.job4j.socialmediaapi.service.UserService;

@Tag(name = "UserController", description = "API управления пользователями")
@RestController
@RequestMapping("/social-media/user")
@Validated
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Получить пользователя по id",
            description = """
                        Получить объект UserDto по его id.
                        Ответом возвращается объект UserDto с полями id, name, email и password.
                        """,
            tags = { "User", "find" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                            @Content(schema = @Schema(implementation = User.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {
                            @Content(schema = @Schema())})
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable("id")
                                            @NotNull
                                            @Min(value = 1, message = "Номер ресурса должен быть больше 1")
                                            int id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Сохранить пользователя",
            description = """
                        Сохранить пользователя, отправив заполненный объект UserDto с 0 id.
                        Ответом возвращается объект UserDto с полями id, name, email и password.
                        """,
            tags = { "User", "save" })
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(schema = @Schema(implementation = User.class), mediaType = "application/json")}),
    })
    @PostMapping()
    public ResponseEntity<UserDto> save(@Valid @RequestBody UserDto userDto) {
        userDto = userService.save(userDto);
        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userDto.getId())
                .toUri();
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(uri)
                .body(userDto);
    }

    @Operation(
            summary = "Удалить пользователя",
            description = """
                        Удаление пользователя по его id.
                        """,
            tags = { "User", "delete" })
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id")
                                       @NotNull
                                       @Min(value = 1, message = "Номер ресурса должен быть больше 1")
                                       int id) {
        if (userService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
