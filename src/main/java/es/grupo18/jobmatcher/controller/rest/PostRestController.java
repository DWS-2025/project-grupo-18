package es.grupo18.jobmatcher.controller.rest;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.grupo18.jobmatcher.dto.PostDTO;
import es.grupo18.jobmatcher.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    @Autowired
    private PostService postService;

    @GetMapping("/")
    public Collection<PostDTO> getAllPosts() {
        return postService.findAll();
    }

    @GetMapping("/{id}")
    public PostDTO getPost(@PathVariable long id) {
        try {
            return postService.findById(id);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Post not found with id: " + id);
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
    public PostDTO updatePost(@PathVariable long id, @RequestBody PostDTO postDTO) {
        try {
            return postService.update(postService.findById(id), postDTO);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Post not found with id: " + id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable long id) {
        try {
            postService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Post not found with id: " + id);
        }
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

}
