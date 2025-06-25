package es.grupo18.jobmatcher.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.grupo18.jobmatcher.dto.PostDTO;
import es.grupo18.jobmatcher.dto.ReviewDTO;
import es.grupo18.jobmatcher.mapper.PostMapper;
import es.grupo18.jobmatcher.mapper.UserMapper;
import es.grupo18.jobmatcher.model.Post;
import es.grupo18.jobmatcher.repository.PostRepository;

import es.grupo18.jobmatcher.util.InputSanitizer;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

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

    @Autowired
    private ReviewService reviewService;

    public Collection<PostDTO> findAll() { // Returns the posts list in reverse order
        return toDTOs(postRepository.findAll());
    }

    public PostDTO findById(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return toDTO(post);
    }

    public PostDTO save(PostDTO postDTO) { // Saves a post
        String safeTitle = InputSanitizer.sanitizePlain(postDTO.title());
        String safeContent = InputSanitizer.sanitizePlain(postDTO.content());
        Post post = toDomain(postDTO);
        post.setTitle(safeTitle);
        post.setContent(safeContent);

        post.setAuthor(userMapper.toDomain(userService.getLoggedUser()));
        post.setTimestamp(LocalDateTime.now());
        postRepository.save(post);
        return toDTO(post);
    }

    public PostDTO create(String title, String content, MultipartFile image) throws IOException {
        byte[] imageBytes = null;
        String contentType = null;

        String safeTitle = InputSanitizer.sanitizePlain(title);
        String safeContent = InputSanitizer.sanitizePlain(content);
        Post post = new Post(safeTitle, safeContent, LocalDateTime.now(), imageBytes,
                userMapper.toDomain(userService.getLoggedUser()));

        post.setImageContentType(contentType);

        if (image != null && !image.isEmpty()) {
            processAndSaveImage(post, image);
        }

        postRepository.save(post);
        return toDTO(post);
    }

    public PostDTO createEmpty() {
        return new PostDTO(null, "", "", LocalDateTime.now(), null, null, null, "", List.of());
    }

    public PostDTO update(PostDTO oldPostDTO, PostDTO updatedPostDTO) {
        Post post = toDomain(oldPostDTO);
        String safeTitle = InputSanitizer.sanitizePlain(updatedPostDTO.title());
        String safeContent = InputSanitizer.sanitizePlain(updatedPostDTO.content());
        post.setTitle(safeTitle);
        post.setContent(safeContent);
        post.setTimestamp(LocalDateTime.now());

        post.setTimestamp(LocalDateTime.now());

        if (updatedPostDTO.image() != null && updatedPostDTO.image().length > 0) {
            post.setImage(updatedPostDTO.image());
            post.setImageContentType(updatedPostDTO.imageContentType());
        }

        postRepository.save(post);
        return toDTO(post);
    }

    public PostDTO update(long id, String title, String content, MultipartFile image) throws IOException {
        long loggedId = userService.getLoggedUser().id();
        if (!canEditOrDeletePost(id, loggedId)) {
            throw new SecurityException("You do not have permission to edit this post");
        }

        Post post = toDomain(findById(id));
        String safeTitle = InputSanitizer.sanitizePlain(title);
        String safeContent = InputSanitizer.sanitizePlain(content);
        post.setTitle(safeTitle);
        post.setContent(safeContent);

        post.setTimestamp(LocalDateTime.now());

        if (image != null && !image.isEmpty()) {
            processAndSaveImage(post, image);
        }

        postRepository.save(post);
        return toDTO(post);
    }

    public void updateImageOnly(Long id, MultipartFile file) throws IOException {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (file != null && !file.isEmpty()) {
            processAndSaveImage(post, file);
            post.setTimestamp(LocalDateTime.now());
            postRepository.save(post);
        } else {
            throw new RuntimeException("File empty");
        }

    }

    private void validateImage(MultipartFile file) throws IOException {
        if (file.isEmpty())
            throw new IllegalArgumentException("File is empty");
        if (file.getSize() > 2 * 1024 * 1024)
            throw new IllegalArgumentException("Maximum allowed: 2MB");

        String contentType = file.getContentType();
        if (!List.of("image/jpeg", "image/png").contains(contentType)) {
            throw new IllegalArgumentException("Only JPEG or PNG are allowed");
        }

        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[8];
            is.read(header);
            if (!(isJpeg(header) || isPng(header))) {
                throw new IllegalArgumentException("Invalid header. The file is not a valid image");
            }
        }

        BufferedImage img = ImageIO.read(file.getInputStream());
        if (img == null) {
            throw new IllegalArgumentException("Image is corrupted or cannot be found");
        }
    }

    private void processAndSaveImage(Post post, MultipartFile image) throws IOException {
        validateImage(image);
        BufferedImage original = ImageIO.read(image.getInputStream());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String format = image.getContentType().equals("image/png") ? "png" : "jpg";
        ImageIO.write(original, format, baos);
        baos.flush();

        post.setImage(baos.toByteArray());
        post.setImageContentType(image.getContentType());
        baos.close();
    }

    private boolean isJpeg(byte[] header) {
        return header[0] == (byte) 0xFF && header[1] == (byte) 0xD8;
    }

    private boolean isPng(byte[] header) {
        return header[0] == (byte) 0x89 && header[1] == (byte) 0x50 &&
                header[2] == (byte) 0x4E && header[3] == (byte) 0x47;
    }

    public void deleteById(long id) {
        long loggedId = userService.getLoggedUser().id();
        if (!canEditOrDeletePost(id, loggedId)) {
            throw new SecurityException("You do not have permission to delete this post");
        }

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        postRepository.delete(post);
    }

    public void delete(PostDTO post) {
        postRepository.deleteById(toDomain(post).getId());
    }

    public boolean canEditImage(Long postId) {
        Long me = userService.getLoggedUser().id();
        return canEditOrDeletePost(postId, me);
    }
    

    public PostDTO toDTO(Post post) {
        List<ReviewDTO> reviews = reviewService.findReviewsByPostId(post.getId());

        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getTimestamp() != null ? post.getTimestamp() : LocalDateTime.now(),
                post.getAuthor() != null ? post.getAuthor().getId() : null,
                post.getImage(),
                post.getImageContentType(),
                post.getAuthor() != null ? post.getAuthor().getName() : "",
                reviews);
    }

    Post toDomain(PostDTO dto) {
        return postMapper.toDomain(dto);
    }

    List<PostDTO> toDTOs(List<Post> posts) {
        return posts.stream().map(this::toDTO).collect(Collectors.toList());
    }

    List<Post> toDomains(List<PostDTO> dtos) {
        return postMapper.toDomains(dtos);
    }

    public void removeImageByPostId(Long id) {
        PostDTO postDTO = findById(id);
        if (postDTO == null) {
            throw new RuntimeException("Post not found");
        }

        if (postDTO.image() == null) {
            throw new RuntimeException("Image not found");
        }
        Post post = toDomain(postDTO);
        post.setImage(null);
        post.setImageContentType(null);
        postRepository.save(post);
    }

    public boolean canEditOrDeletePost(Long postId, Long userId) {
        return postRepository.findById(postId)
                .map(post -> {
                    System.out.println(">>> POST OWNER: " + post.getAuthor().getId() + " | REQUEST USER: " + userId);
                    return post.getAuthor().getId().equals(userId)
                            || userService.hasRoleById(userId, "ADMIN");
                })
                .orElse(false);
    }

    public List<PostDTO> findFilteredPosts(String sort,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String title) {

        Example<Post> example;
        if (title != null && !title.isBlank()) {

            Post probe = new Post();
            probe.setTitle(title);
            probe.setContent(title);

            ExampleMatcher matcher = ExampleMatcher.matchingAny()
                    .withIgnoreCase()
                    .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains())
                    .withMatcher("content", ExampleMatcher.GenericPropertyMatchers.contains())
                    .withIgnorePaths("id", "timestamp", "image", "author", "reviews");

            example = Example.of(probe, matcher);

        } else {
            example = Example.of(new Post());
        }

        List<Post> posts = (title != null && !title.isBlank())
                ? postRepository.findAll(example)
                : postRepository.findAll();

        if (fromDate != null) {
            posts = posts.stream()
                    .filter(p -> p.getTimestamp() != null &&
                            !p.getTimestamp().isBefore(fromDate))
                    .collect(Collectors.toList());

        }
        if (toDate != null) {
            posts = posts.stream()
                    .filter(p -> p.getTimestamp() != null &&
                            !p.getTimestamp().isAfter(toDate))
                    .collect(Collectors.toList());

        }

        if ("asc".equalsIgnoreCase(sort)) {
            posts.sort(Comparator.comparing(Post::getTimestamp,
                    Comparator.nullsLast(Comparator.naturalOrder())));
        } else if ("desc".equalsIgnoreCase(sort)) {
            posts.sort(Comparator.comparing(Post::getTimestamp,
                    Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        }

        return posts.stream()
                .map(post -> new PostDTO(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getTimestamp(),
                        post.getAuthor() != null ? post.getAuthor().getId() : null,
                        post.getImage(),
                        post.getImageContentType(),
                        post.getAuthor() != null ? post.getAuthor().getName() : null,
                        post.getReviews() != null
                                ? post.getReviews().stream()
                                        .map(review -> reviewService.toDTO(review))
                                        .collect(Collectors.toList())
                                : List.of()))
                .collect(Collectors.toList());

    }

}
