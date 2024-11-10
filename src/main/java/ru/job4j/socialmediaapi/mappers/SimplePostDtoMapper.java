package ru.job4j.socialmediaapi.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.job4j.socialmediaapi.dto.SimplePostDto;
import ru.job4j.socialmediaapi.model.Post;

@Mapper(componentModel = "spring")
public interface SimplePostDtoMapper {
    @Mapping(source = "id", target = "postId")
    @Mapping(source = "title", target = "title")
    SimplePostDto getSimplePostDtoFromPost(Post post);
}
