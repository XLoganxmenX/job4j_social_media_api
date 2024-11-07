package ru.job4j.socialmediaapi.mappers;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.job4j.socialmediaapi.dto.UserDto;
import ru.job4j.socialmediaapi.dto.UserListDto;
import ru.job4j.socialmediaapi.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    UserDto getUserDtoFromUser(User user);

    @Mapping(target = "timezone", ignore = true)
    @Mapping(target = "userRelates", ignore = true)
    User getUserFromUserDto(UserDto userDto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    UserListDto getUserListDtoFromUser(User user);
}
