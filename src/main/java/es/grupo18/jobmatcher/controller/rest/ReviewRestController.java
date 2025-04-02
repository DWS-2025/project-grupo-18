package es.grupo18.jobmatcher.controller.rest;

import es.grupo18.jobmatcher.dto.ReviewDTO;
import es.grupo18.jobmatcher.mapper.ReviewMapper;
import es.grupo18.jobmatcher.model.Review;
import es.grupo18.jobmatcher.service.PostService;
import es.grupo18.jobmatcher.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/reviews")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PostService postService;

    @Autowired
    private ReviewMapper reviewMapper;

    @GetMapping
    public List<ReviewDTO> getAll() {
        return reviewMapper.toDTOs(reviewService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getById(@PathVariable Long id) {
        return reviewService.findById(id)
                .map(review -> ResponseEntity.ok(reviewMapper.toDTO(review)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<ReviewDTO> create(@PathVariable Long postId, @RequestBody ReviewDTO dto) {
        Review review = reviewMapper.toEntity(dto);
        postService.findById(postId).ifPresent(post -> reviewService.save(post, review));
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(review.getId()).toUri();
        return ResponseEntity.created(location).body(reviewMapper.toDTO(review));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> update(@PathVariable Long id, @RequestBody ReviewDTO dto) {
        Review existing = reviewService.findById(id).orElse(null);
        if (existing == null) return ResponseEntity.notFound().build();
        Review updated = reviewMapper.toEntity(dto);
        updated.setId(id);
        reviewService.update(existing, updated);
        return ResponseEntity.ok(reviewMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReviewDTO> delete(@PathVariable Long id) {
        Review review = reviewService.findById(id).orElse(null);
        if (review == null) return ResponseEntity.notFound().build();
        reviewService.delete(review.getId(), review.getPost());
        return ResponseEntity.ok(reviewMapper.toDTO(review));
    }
}