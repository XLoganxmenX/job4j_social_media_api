package ru.job4j.socialmediaapi.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.dto.UserDto;
import ru.job4j.socialmediaapi.dto.UserListDto;
import ru.job4j.socialmediaapi.mappers.UserMapper;
import ru.job4j.socialmediaapi.model.User;
import ru.job4j.socialmediaapi.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional()
    public UserDto save(UserDto userDto) throws DataAccessException {
        User user = userMapper.getUserFromUserDto(userDto);
        userRepository.save(user);
        return userMapper.getUserDtoFromUser(user);
    }

    @Override
    public Optional<UserDto> findById(int id) throws DataAccessException {
        return userRepository.findById(id)
                        .map(userMapper::getUserDtoFromUser);
    }

    @Override
    public Optional<UserDto> findByEmailAndPassword(UserDto userDto) throws DataAccessException {
        return userRepository.findByEmailAndPassword(userDto.getEmail(), userDto.getPassword())
                .map(userMapper::getUserDtoFromUser);
    }

    @Override
    public boolean delete(int id) throws DataAccessException {
        return userRepository.delete(id) > 0;
    }

    @Override
    public List<UserListDto> findAll() throws DataAccessException {
        return userRepository.findAll().stream()
                .map(userMapper::getUserListDtoFromUser)
                .collect(Collectors.toList());
    }
}
