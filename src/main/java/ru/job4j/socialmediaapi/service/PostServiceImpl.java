package ru.job4j.socialmediaapi.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmediaapi.dto.PostPhotoDto;
import ru.job4j.socialmediaapi.model.Post;
import ru.job4j.socialmediaapi.model.PostPhoto;
import ru.job4j.socialmediaapi.model.User;
import ru.job4j.socialmediaapi.repository.PostRepository;

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

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Post createPost(String title, String description,
                           User user, List<PostPhotoDto> postPhotos) throws DataAccessException {
        List<PostPhoto> savedPostPhotos = postPhotoService.saveAll(postPhotos);
        Post post = new Post(0, title, description, user, LocalDateTime.now(ZoneId.of("UTC")), savedPostPhotos);
        return postRepository.save(post);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deletePostById(int id) throws DataAccessException {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post with id " + id + " not found"));
        post.getPostPhotos().forEach(postPhoto ->
                postPhotoService.deletePostPhotoById(postPhoto.getId()));
        postRepository.deletePostById(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateTitleAndDescription(String title, String description, int id) throws DataAccessException {
        postRepository.updateTitleAndDescriptionById(title, description, id);
    }
}
