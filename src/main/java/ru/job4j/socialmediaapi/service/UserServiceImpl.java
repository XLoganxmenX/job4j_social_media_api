package ru.job4j.socialmediaapi.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.dto.UserDto;
import ru.job4j.socialmediaapi.dto.UserListDto;
import ru.job4j.socialmediaapi.dto.request.SignupRequestDTO;
import ru.job4j.socialmediaapi.dto.response.RegisterDTO;
import ru.job4j.socialmediaapi.enums.ERole;
import ru.job4j.socialmediaapi.mappers.UserMapper;
import ru.job4j.socialmediaapi.model.Role;
import ru.job4j.socialmediaapi.model.User;
import ru.job4j.socialmediaapi.repository.RoleRepository;
import ru.job4j.socialmediaapi.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private PasswordEncoder encoder;
    private final RoleRepository roleRepository;

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
    public boolean delete(int id) throws DataAccessException {
        return userRepository.delete(id) > 0;
    }

    @Override
    public List<UserListDto> findAll() throws DataAccessException {
        return userRepository.findAll().stream()
                .map(userMapper::getUserListDtoFromUser)
                .collect(Collectors.toList());
    }

    public RegisterDTO signUp(SignupRequestDTO signUpRequest) {
        if (Boolean.TRUE.equals(userRepository.existsByName(signUpRequest.getUsername()))
                || Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
            return new RegisterDTO(HttpStatus.BAD_REQUEST, "Error: Name or Email is already taken!");
        }

        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())
        );

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        Supplier<RuntimeException> supplier = () -> new RuntimeException("Error: Role is not found.");

        if (strRoles == null) {
            roles.add(roleRepository.findByName(ERole.ROLE_USER).orElseThrow(supplier));
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin" -> roles.add(roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(supplier));
                    case "mod" -> roles.add(roleRepository.findByName(ERole.ROLE_MODERATOR).orElseThrow(supplier));
                    default -> roles.add(roleRepository.findByName(ERole.ROLE_USER).orElseThrow(supplier));
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return new RegisterDTO(HttpStatus.OK, "User registered successfully!");
    }
}
