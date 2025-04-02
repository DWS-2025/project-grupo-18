package es.grupo18.jobmatcher.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.grupo18.jobmatcher.dto.PostDTO;
import es.grupo18.jobmatcher.mapper.PostMapper;
import es.grupo18.jobmatcher.mapper.UserMapper;
import es.grupo18.jobmatcher.model.Post;
import es.grupo18.jobmatcher.repository.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    public Collection<PostDTO> findAllWithAuthors() {
        return toDTOs(postRepository.findAllWithAuthors());
    }

    public Collection<PostDTO> findAll() {
        return toDTOs(postRepository.findAll());
    }

    public PostDTO findById(long id) {
        Optional<Post> post = postRepository.findById(id);
        return post.map(this::toDTO).orElse(null);
    }

    public PostDTO save(PostDTO postDTO) {
        Post post = toDomain(postDTO);
        post.setAuthor(userMapper.toDomain(userService.getLoggedUser()));
        postRepository.save(post);
        return toDTO(post);
    }

    public PostDTO update(PostDTO oldPostDTO, PostDTO updatedPostDTO) {
        Post post = toDomain(oldPostDTO);
        post.setTitle(updatedPostDTO.title());
        post.setContent(updatedPostDTO.content());
        post.setTimestamp(LocalDateTime.parse(updatedPostDTO.timestamp()));

        if (updatedPostDTO.image() != null && updatedPostDTO.image().length > 0) {
            post.setImage(updatedPostDTO.image());
            post.setImageContentType(updatedPostDTO.imageContentType());
        }

        postRepository.save(post);
        return toDTO(post);
    }

    public void deleteById(long id) {
        postRepository.deleteById(id);
    }

    public PostDTO delete(PostDTO post) {
        postRepository.deleteById(toDomain(post).getId());
        return post;
    }

    public Collection<PostDTO> findFilteredPosts(String sort, LocalDateTime from, LocalDateTime to, String title) {
        List<Post> posts = new ArrayList<>(postRepository.findAll());

        if (from != null && to != null) {
            posts = posts.stream()
                    .filter(p -> !p.getTimestamp().isBefore(from) && !p.getTimestamp().isAfter(to))
                    .collect(Collectors.toList());
        } else if (from != null) {
            posts = posts.stream()
                    .filter(p -> !p.getTimestamp().isBefore(from))
                    .collect(Collectors.toList());
        } else if (to != null) {
            posts = posts.stream()
                    .filter(p -> !p.getTimestamp().isAfter(to))
                    .collect(Collectors.toList());
        }

        if (title != null && !title.isBlank()) {
            posts = posts.stream()
                    .filter(p -> p.getTitle() != null && p.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if ("desc".equals(sort)) {
            posts.sort((p1, p2) -> p2.getTimestamp().compareTo(p1.getTimestamp()));
        } else if ("asc".equals(sort)) {
            posts.sort(Comparator.comparing(Post::getTimestamp));
        }

        return toDTOs(posts);
    }

    private PostDTO toDTO(Post post) {
        return postMapper.toDTO(post);
    }

    private Post toDomain(PostDTO dto) {
        return postMapper.toDomain(dto);
    }

    private List<PostDTO> toDTOs(List<Post> posts) {
        return postMapper.toDTOs(posts);
    }

} 
