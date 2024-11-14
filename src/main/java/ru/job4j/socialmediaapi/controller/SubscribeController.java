package ru.job4j.socialmediaapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.job4j.socialmediaapi.dto.UserListDto;
import ru.job4j.socialmediaapi.service.SubscribeService;

import java.util.List;

@Tag(name = "SubscribeController", description = "API управления взаимоотношениями пользователей")
@RestController
@RequestMapping("/social-media/relations")
@Validated
@AllArgsConstructor
public class SubscribeController {
    private final SubscribeService subscribeService;

    @Operation(
            summary = "Отправить запрос на дружбу другому пользователю (подписаться)",
            description = """
                        Отправить запрос от id пользователя на дружбу другому пользователю по его id.
                        Реализуется подписка на пользователя.
                        """,
            tags = { "User", "subscribe" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema())}),
    })
    @PostMapping("/send-friend-request")
    public ResponseEntity<Void> sendRequestToFriendships(@NotNull
                                                         @Min(value = 1, message = "Номер ресурса должен быть больше 0")
                                                         @RequestParam(name = "userId") int userId,
                                                         @NotNull
                                                         @Min(value = 1, message = "Номер ресурса должен быть больше 0")
                                                         @RequestParam(name = "relatedUserId") int relatedUserId) {
        subscribeService.sendRequestToFriendships(userId, relatedUserId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(
            summary = "Получить всех подписчиков пользователя",
            description = """
                        Получить список всех подписчиков пользователя по его id.
                        Ответом возвращается список объектов UserListDto с полями id и name.
                        """,
            tags = { "User", "subscribe" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserListDto.class)))}),
    })
    @GetMapping("/user/{userId}/subscribers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserListDto> getSubscribersForUser(@PathVariable("userId")
                                                   @NotNull
                                                   @Min(value = 1, message = "Номер ресурса должен быть больше 1")
                                                   int userId) {
        return subscribeService.getSubscribersForUser(userId);
    }

    @Operation(
            summary = "Подтвердить дружбу с пользователем",
            description = """
                        Подтвердить дружбу пользователя A с пользователем B по их id.
                        Реализуется взаимная подписка.
                        """,
            tags = { "User", "subscribe" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema())}),
    })
    @PostMapping("/confirm-friend-request")
    public ResponseEntity<Void> confirmFriendships(@NotNull
                                                   @Min(value = 1, message = "Номер ресурса должен быть больше 0")
                                                   @RequestParam(name = "userId") int userId,
                                                   @NotNull
                                                   @Min(value = 1, message = "Номер ресурса должен быть больше 0")
                                                   @RequestParam(name = "friendId") int friendId) {
        subscribeService.confirmFriendships(userId, friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
