package ru.job4j.socialmediaapi.service;

import ru.job4j.socialmediaapi.dto.UserDto;
import ru.job4j.socialmediaapi.dto.UserListDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto save(UserDto user);

    Optional<UserDto> findById(int id);

    Optional<UserDto> findByEmailAndPassword(UserDto userDto);

    boolean delete(int id);

    List<UserListDto> findAll();
}
