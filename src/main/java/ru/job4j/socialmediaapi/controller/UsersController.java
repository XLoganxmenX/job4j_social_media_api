package ru.job4j.socialmediaapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.socialmediaapi.dto.UserListDto;
import ru.job4j.socialmediaapi.service.UserService;

import java.util.List;

@Tag(name = "UserControllers", description = "API управления коллекциями пользователей")
@RestController
@RequestMapping("/social-media/users")
@AllArgsConstructor
public class UsersController {
    private final UserService userService;

    @Operation(
            summary = "Получить всех пользоватей",
            description = """
                        Получить список всех пользователей в формате объектов UserListDto.
                        Ответом возвращается список всех объектов UserListDto с полями id и name.
                        """,
            tags = { "User", "find" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json",
                             array = @ArraySchema(schema = @Schema(implementation = UserListDto.class)))}),
    })
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<UserListDto> findAll() {
        return userService.findAll();
    }
}
