package ru.job4j.socialmediaapi.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.socialmediaapi.dto.UserListDto;
import ru.job4j.socialmediaapi.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/social-media/users")
@AllArgsConstructor
public class UsersController {
    private final UserService userService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<UserListDto> findAll() {
        return userService.findAll();
    }
}
