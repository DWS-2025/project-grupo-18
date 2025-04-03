package es.grupo18.jobmatcher.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.grupo18.jobmatcher.dto.PostDTO;
import es.grupo18.jobmatcher.dto.ReviewDTO;
import es.grupo18.jobmatcher.mapper.PostMapper;
import es.grupo18.jobmatcher.mapper.ReviewMapper;
import es.grupo18.jobmatcher.mapper.UserMapper;
import es.grupo18.jobmatcher.model.Post;
import es.grupo18.jobmatcher.model.Review;
import es.grupo18.jobmatcher.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewMapper reviewMapper;

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
        Optional<Review> review = reviewRepository.findById(id);
        return review.map(this::toDTO).orElse(null);
    }

    public ReviewDTO save(PostDTO postDTO, ReviewDTO reviewDTO) { // Saves a review
        Review review = toDomain(reviewDTO);
        Post post = postMapper.toDomain(postDTO);
        post.getReviews().add(review);
        review.setAuthor(userMapper.toDomain(userService.getLoggedUser()));
        reviewRepository.save(review);
        return toDTO(review);
    }

    public ReviewDTO update(ReviewDTO oldReviewDTO, ReviewDTO updatedReviewDTO) {
        Review oldReview = toDomain(oldReviewDTO);
        Review updatedReview = toDomain(updatedReviewDTO);
        oldReview.setText(updatedReview.getText());
        oldReview.setRating(updatedReview.getRating());
        reviewRepository.save(oldReview);
        return toDTO(oldReview);
    }

    public void deleteById(long id) { // Deletes a review by its id

    }

    public ReviewDTO delete(Long reviewId, PostDTO postDTO) {
        ReviewDTO reviewDTO = findById(reviewId);
        if (reviewDTO == null)
            return null;
        Review review = toDomain(reviewDTO);
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
    

}
