package es.grupo18.jobmatcher.controller.rest;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDTO) {
        postDTO = postService.save(postDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(postDTO.id())
                .toUri();
        return ResponseEntity.created(location).body(postDTO);
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

        LocalDateTime fromDate = from != null ? LocalDateTime.parse(from, DateTimeFormatter.ISO_DATE_TIME) : null;
        LocalDateTime toDate = to != null ? LocalDateTime.parse(to, DateTimeFormatter.ISO_DATE_TIME) : null;

        return postService.findFilteredPosts(sort, fromDate, toDate, title);
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
