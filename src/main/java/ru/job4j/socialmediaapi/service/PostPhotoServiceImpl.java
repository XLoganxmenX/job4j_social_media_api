package ru.job4j.socialmediaapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.dto.PostPhotoDto;
import ru.job4j.socialmediaapi.model.PostPhoto;
import ru.job4j.socialmediaapi.repository.PostPhotoRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostPhotoServiceImpl implements PostPhotoService {
    private final PostPhotoRepository postPhotoRepository;
    private final String storageDirectory;

    public PostPhotoServiceImpl(PostPhotoRepository postPhotoRepository,
                                @Value("${photo.directory:photos}") String storageDirectory) {
        this.postPhotoRepository = postPhotoRepository;
        this.storageDirectory = storageDirectory;
        createStorageDirectory(storageDirectory);
    }

    private void createStorageDirectory(String path) {
        try {
            Files.createDirectories(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<PostPhoto> saveAll(List<PostPhotoDto> postPhotos) throws DataAccessException {
        return postPhotos.stream()
                .map(this::save)
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private PostPhoto save(PostPhotoDto postPhotoDto) throws DataAccessException {
        var path = getNewFilePath(postPhotoDto.getName());
        writeFileBytes(path, decode(postPhotoDto.getBase64Content()));
        return postPhotoRepository.save(new PostPhoto(0, postPhotoDto.getName(), path));
    }

    private String getNewFilePath(String sourceName) {
        return storageDirectory + java.io.File.separator + UUID.randomUUID() + sourceName;
    }

    private void writeFileBytes(String path, byte[] content) {
        try {
            Files.write(Path.of(path), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<PostPhotoDto> getById(int id) throws DataAccessException {
        var postPhotoOptional = postPhotoRepository.findById(id);
        if (postPhotoOptional.isEmpty()) {
            return Optional.empty();
        }
        var content = readFileAsBytes(postPhotoOptional.get().getPath());
        return Optional.of(new PostPhotoDto(postPhotoOptional.get().getName(), encode(content)));
    }

    private byte[] readFileAsBytes(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PostPhotoDto createPostPhotoDtoFromPostPhoto(PostPhoto postPhoto) throws DataAccessException {
        var content = readFileAsBytes(postPhoto.getPath());
        return new PostPhotoDto(postPhoto.getName(), encode(content));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deletePostPhotoById(int id) throws DataAccessException {
        var postPhotoOptional = postPhotoRepository.findById(id);
        if (postPhotoOptional.isPresent()) {
            deleteFile(postPhotoOptional.get().getPath());
            postPhotoRepository.deletePostPhotoById(id);
        }
    }

    private void deleteFile(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] decode(String content) {
        return Base64.getDecoder().decode(content);
    }

    private String encode(byte[] content) {
        return Base64.getEncoder().encodeToString(content);
    }
}
