package ru.job4j.socialmediaapi.service;

import ru.job4j.socialmediaapi.dto.UserDto;
import ru.job4j.socialmediaapi.dto.UserListDto;
import ru.job4j.socialmediaapi.dto.request.SignupRequestDTO;
import ru.job4j.socialmediaapi.dto.response.RegisterDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto save(UserDto user);

    Optional<UserDto> findById(int id);

    boolean delete(int id);

    List<UserListDto> findAll();

    RegisterDTO signUp(SignupRequestDTO signUpRequest);
}
