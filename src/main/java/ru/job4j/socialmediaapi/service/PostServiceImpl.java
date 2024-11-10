package ru.job4j.socialmediaapi.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.dto.PostDto;
import ru.job4j.socialmediaapi.dto.PostPhotoDto;
import ru.job4j.socialmediaapi.dto.SimplePostDto;
import ru.job4j.socialmediaapi.dto.UserPostsDto;
import ru.job4j.socialmediaapi.mappers.SimplePostDtoMapper;
import ru.job4j.socialmediaapi.model.Post;
import ru.job4j.socialmediaapi.model.PostPhoto;
import ru.job4j.socialmediaapi.model.User;
import ru.job4j.socialmediaapi.repository.PostRepository;
import ru.job4j.socialmediaapi.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostPhotoService postPhotoService;
    private final UserRepository userRepository;
    private final SimplePostDtoMapper simplePostDtoMapper;

    @Override
    public Optional<PostDto> findById(int id) throws DataAccessException {
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
    public PostDto createPost(PostDto postDto) throws DataAccessException, NoSuchElementException {
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
    public boolean deletePostById(int id) throws DataAccessException, NoSuchElementException {
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

    @Override
    public List<UserPostsDto> getUserPostsDtosByUserIds(List<Integer> userIds) {
        List<Post> posts = postRepository.findByUserIds(userIds);
        Map<User, List<Post>> usersPosts = posts.stream()
                .collect(Collectors.groupingBy(Post::getUser));
        return usersPosts.entrySet().stream()
                .map(this::mapToUserPostsDto)
                .collect(Collectors.toList());
    }

    private UserPostsDto mapToUserPostsDto(Map.Entry<User, List<Post>> entry) {
        User user = entry.getKey();
        List<SimplePostDto> simplePostDtos = mapPostsToSimplePostDtos(entry.getValue());
        return new UserPostsDto(user.getId(), user.getName(), simplePostDtos);
    }

    private List<SimplePostDto> mapPostsToSimplePostDtos(List<Post> posts) {
        return posts.stream()
                .map(simplePostDtoMapper::getSimplePostDtoFromPost)
                .collect(Collectors.toList());
    }
}
