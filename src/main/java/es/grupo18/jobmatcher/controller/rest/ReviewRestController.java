package es.grupo18.jobmatcher.controller.rest;

import es.grupo18.jobmatcher.dto.PostDTO;
import es.grupo18.jobmatcher.dto.ReviewDTO;
import es.grupo18.jobmatcher.service.PostService;
import es.grupo18.jobmatcher.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/reviews")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PostService postService;

    @GetMapping
    public Collection<ReviewDTO> getAll() {
        return reviewService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getById(@PathVariable Long id) {
        ReviewDTO review = reviewService.findById(id);
        return (review == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(review);
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<ReviewDTO> create(@PathVariable Long postId, @RequestBody ReviewDTO dto) {
        PostDTO post = postService.findById(postId);
        if (post == null)
            return ResponseEntity.notFound().build();
        ReviewDTO created = reviewService.save(post, dto);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> update(@PathVariable Long id, @RequestBody ReviewDTO dto) {
        ReviewDTO existing = reviewService.findById(id);
        if (existing == null)
            return ResponseEntity.notFound().build();
        ReviewDTO updated = new ReviewDTO(id, dto.text(), dto.rating(), existing.authorId(), existing.postId());
        reviewService.update(existing, updated);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReviewDTO> delete(@PathVariable Long id) {
        ReviewDTO review = reviewService.findById(id);
        if (review == null)
            return ResponseEntity.notFound().build();
        PostDTO post = postService.findById(review.postId());
        reviewService.delete(id, post);
        return ResponseEntity.ok(review);
    }

}
