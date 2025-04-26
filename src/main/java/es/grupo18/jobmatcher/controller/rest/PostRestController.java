package es.grupo18.jobmatcher.controller.rest;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

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
    public ResponseEntity<PostDTO> updatePost(@PathVariable long id, @RequestBody PostDTO postDTO) {
        try {
            return ResponseEntity.ok(postService.update(postService.findById(id), postDTO));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PostDTO> deletePost(@PathVariable long id) {
        try {
            postService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
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
