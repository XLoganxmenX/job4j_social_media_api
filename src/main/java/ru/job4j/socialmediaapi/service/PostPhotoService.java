package ru.job4j.socialmediaapi.service;

import ru.job4j.socialmediaapi.dto.PostPhotoDto;
import ru.job4j.socialmediaapi.model.PostPhoto;

import java.util.List;
import java.util.Optional;

public interface PostPhotoService {
    List<PostPhoto> saveAll(List<PostPhotoDto> postPhotos);

    Optional<PostPhotoDto> getById(int id);

    PostPhotoDto createPostPhotoDtoFromPostPhoto(PostPhoto postPhoto);

    void deletePostPhotoById(int id);
}
