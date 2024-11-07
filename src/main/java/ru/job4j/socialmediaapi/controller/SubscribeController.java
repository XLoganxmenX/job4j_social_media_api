package ru.job4j.socialmediaapi.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.job4j.socialmediaapi.dto.UserListDto;
import ru.job4j.socialmediaapi.service.SubscribeService;

import java.util.List;

@RestController
@RequestMapping("/social-media/relations")
@AllArgsConstructor
public class SubscribeController {
    private final SubscribeService subscribeService;

    @PostMapping("/send-friend-request")
    public ResponseEntity<Void> sendRequestToFriendships(@RequestParam int userId,
                                                         @RequestParam int relatedUserID) {
        subscribeService.sendRequestToFriendships(userId, relatedUserID);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/user/{userId}/subscribers")
    public List<UserListDto> getSubscribersForUser(@PathVariable("userId")
                                                   @NotNull
                                                   @Min(value = 1, message = "Номер ресурса должен быть больше 1")
                                                   int userId) {
        return subscribeService.getSubscribersForUser(userId);
    }

    @PostMapping("/confirm-friend-request")
    public ResponseEntity<Void> confirmFriendships(@RequestParam int userId,
                                                   @RequestParam int friendId) {
        subscribeService.confirmFriendships(userId, friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
