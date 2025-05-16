package es.grupo18.jobmatcher.controller.rest;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.grupo18.jobmatcher.dto.PostDTO;
import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.service.PostService;
import es.grupo18.jobmatcher.service.UserService;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {
    private static final PolicyFactory TEXT_SANITIZER = Sanitizers.FORMATTING;

    private String sanitizeText(String text) {
        return text != null ? TEXT_SANITIZER.sanitize(text) : null;
    }

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public Collection<PostDTO> getAllPosts() {
        return postService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable long id) {
        try {
            return ResponseEntity.ok(postService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDTO) {
        Long authUserId = getAuthenticatedUserId();

        PostDTO sanitizedDto = new PostDTO(
                postDTO.id(),
                sanitizeText(postDTO.title()),
                sanitizeText(postDTO.content()),
                LocalDateTime.now(),
                authUserId,
                postDTO.image(),
                postDTO.imageContentType(),
                sanitizeText(postDTO.authorName()),
                postDTO.reviews() != null ? postDTO.reviews() : List.of());

        sanitizedDto = postService.save(sanitizedDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(sanitizedDto.id())
                .toUri();
        return ResponseEntity.created(location).body(sanitizedDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable long id,
            @RequestBody PostDTO postDTO) {
        Long authUserId = getAuthenticatedUserId();

        if (!postService.canEditOrDeletePost(id, authUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to edit this post");
        }
        try {
            PostDTO existing = postService.findById(id);
            PostDTO updated = postService.update(existing, postDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + id + " not found.", e);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @postService.canEditOrDeletePost(#id)")
    public ResponseEntity<Void> deletePost(@PathVariable long id) {
        Long authUserId = getAuthenticatedUserId();

        if (!postService.canEditOrDeletePost(id, authUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this post.");
        }

        postService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public Collection<PostDTO> filterPosts(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String title) {
        if (sort != null && !sort.matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("Invalid sort parameter");
        }
        String safeTitle = sanitizeText(title);
        LocalDateTime fromDate = null, toDate = null;
        try {
            if (from != null)
                fromDate = LocalDateTime.parse(from, DateTimeFormatter.ISO_DATE_TIME);
            if (to != null)
                toDate = LocalDateTime.parse(to, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format");
        }
        return postService.findFilteredPosts(sort, fromDate, toDate, safeTitle);
    }

    private Long getAuthenticatedUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return user.getId();
        } else {
            throw new IllegalArgumentException("User not authenticated");
        }
    }

}
