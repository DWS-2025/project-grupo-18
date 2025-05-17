package es.grupo18.jobmatcher.service;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.grupo18.jobmatcher.dto.PostDTO;
import es.grupo18.jobmatcher.dto.ReviewDTO;
import es.grupo18.jobmatcher.mapper.PostMapper;
import es.grupo18.jobmatcher.mapper.ReviewMapper;
import es.grupo18.jobmatcher.mapper.UserMapper;
import es.grupo18.jobmatcher.model.Post;
import es.grupo18.jobmatcher.model.Review;
import es.grupo18.jobmatcher.repository.PostRepository;
import es.grupo18.jobmatcher.repository.ReviewRepository;
import es.grupo18.jobmatcher.util.InputSanitizer;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    public Collection<ReviewDTO> findAll() {
        return toDTOs(reviewRepository.findAll());
    }

    public ReviewDTO findById(long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return toDTO(review);
    }

    public ReviewDTO save(PostDTO postDTO, ReviewDTO reviewDTO) { // Saves a review
        ReviewDTO cleanDto = new ReviewDTO(
                reviewDTO.id(),
                InputSanitizer.sanitizePlain(reviewDTO.text()),
                reviewDTO.rating(),
                reviewDTO.authorId(),
                reviewDTO.postId(),
                reviewDTO.authorName());
        Review review = toDomain(cleanDto);

        Post post = postMapper.toDomain(postDTO);
        post.getReviews().add(review);
        review.setAuthor(userMapper.toDomain(userService.getLoggedUser()));
        reviewRepository.save(review);
        return toDTO(review);
    }

    public ReviewDTO create(long postId, String text, int rating) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));

        String safeText = InputSanitizer.sanitizePlain(text);
        Review review = new Review(safeText, rating);
        review.setAuthor(userMapper.toDomain(userService.getLoggedUser()));
        review.setPost(post);

        reviewRepository.save(review);
        return toDTO(review);
    }

    public ReviewDTO update(ReviewDTO oldReviewDTO, ReviewDTO updatedReviewDTO) {
        Review oldReview = toDomain(oldReviewDTO);
        Review updatedReview = toDomain(updatedReviewDTO);
        String safeText = InputSanitizer.sanitizePlain(updatedReview.getText());
        oldReview.setText(safeText);

        oldReview.setRating(updatedReview.getRating());
        reviewRepository.save(oldReview);
        return toDTO(oldReview);
    }

    public ReviewDTO update(long reviewId, String text, int rating) {
        String email = userService.getLoggedUser().email();
        if (!canEditOrDeleteReview(reviewId, email)) {
            throw new SecurityException("You do not have permission to edit this review");
        }

        Review review = toDomain(findById(reviewId));
        String safeText = InputSanitizer.sanitizePlain(text);
        review.setText(safeText);

        review.setRating(rating);
        reviewRepository.save(review);
        return toDTO(review);
    }

    public void deleteById(long id) {
        String email = userService.getLoggedUser().email();
        if (!canEditOrDeleteReview(id, email)) {
            throw new SecurityException("You do not have permission to delete this review");
        }

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        Post post = review.getPost();
        if (post != null) {
            post.getReviews().removeIf(r -> r.getId() == id);
            postRepository.save(post);
        }
        reviewRepository.delete(review);
    }

    public ReviewDTO delete(Long reviewId, PostDTO postDTO) {
        ReviewDTO reviewDTO = findById(reviewId);
        if (reviewDTO == null)
            return null;
        Review review = toDomain(reviewDTO);
        Post post = postRepository.findById(postDTO.id())
                .orElseThrow(() -> new NoSuchElementException("Post not found"));

        post.getReviews().removeIf(r -> r.getId() == reviewId);
        postRepository.save(post);
        reviewRepository.delete(review);

        return reviewDTO;
    }

    private ReviewDTO toDTO(Review review) {
        return reviewMapper.toDTO(review);
    }

    private Review toDomain(ReviewDTO dto) {
        return reviewMapper.toDomain(dto);
    }

    private List<ReviewDTO> toDTOs(List<Review> reviews) {
        return reviewMapper.toDTOs(reviews);
    }

    public List<ReviewDTO> findReviewsByPostId(Long postId) {
        return toDTOs(reviewRepository.findByPostId(postId));
    }

    public boolean canEditOrDeleteReview(Long reviewId, String username) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("Review no encontrada"));
        return review.getAuthor().getEmail().equals(username)
                || userService.hasRole(username, "ADMIN");
    }

    public boolean canModifyReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        Long currentUserId = userService.getLoggedUser().id();
        boolean isOwner = review.getAuthor().getId().equals(currentUserId);
        boolean isAdmin = userService.getLoggedUser().roles().contains("ROLE_ADMIN");
        return isOwner || isAdmin;
    }

}
