package ru.job4j.socialmediaapi.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.dto.PostDto;
import ru.job4j.socialmediaapi.dto.PostPhotoDto;
import ru.job4j.socialmediaapi.model.Post;
import ru.job4j.socialmediaapi.model.PostPhoto;
import ru.job4j.socialmediaapi.model.User;
import ru.job4j.socialmediaapi.repository.PostRepository;
import ru.job4j.socialmediaapi.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostPhotoService postPhotoService;
    private final UserRepository userRepository;

    @Override
    public Optional<PostDto> findById(int id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            return Optional.empty();
        }

        List<PostPhotoDto> postPhotoDtos = post.get().getPostPhotos()
                .stream()
                .map(postPhotoService::createPostPhotoDtoFromPostPhoto)
                .toList();
        PostDto postDto = new PostDto(post.get(), postPhotoDtos);
        return Optional.of(postDto);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PostDto createPost(PostDto postDto) throws DataAccessException {
        User user = userRepository.findById(postDto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + postDto.getUserId() + " not found"));
        List<PostPhoto> savedPostPhotos = postPhotoService.saveAll(postDto.getPostPhotos());
        Post post = postRepository.save(
                new Post(
                    0,
                    postDto.getTitle(),
                    postDto.getDescription(),
                    user,
                    LocalDateTime.now(ZoneId.of("UTC")),
                    savedPostPhotos
                )
        );
        postDto.setId(post.getId());
        return postDto;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deletePostById(int id) throws DataAccessException {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post with id " + id + " not found"));
        post.getPostPhotos().forEach(postPhoto ->
                postPhotoService.deletePostPhotoById(postPhoto.getId()));
        return postRepository.deletePostById(id) > 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateTitleAndDescription(String title, String description, int id) throws DataAccessException {
        postRepository.updateTitleAndDescriptionById(title, description, id);
    }
}
