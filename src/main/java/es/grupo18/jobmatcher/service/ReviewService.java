package es.grupo18.jobmatcher.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.grupo18.jobmatcher.model.Post;
import es.grupo18.jobmatcher.model.Review;
import es.grupo18.jobmatcher.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Optional<Review> findById(long id) { // Returns a review by its id
        return reviewRepository.findById(id);
    }

    public void save(Post post, Review review) { // Saves a review
        post.getReviews().add(review);
        review.setAuthor(userService.getLoggedUser());
        reviewRepository.save(review);
    }

    public void update(Review oldReview, Review updatedReview) {
        oldReview.setText(updatedReview.getText());
        oldReview.setRating(updatedReview.getRating());
        reviewRepository.save(oldReview);
    }

    public void deleteById(long id) { // Deletes a review by its id

    }

    public void delete(Long reviewId, Post post) { // Deletes a review
        Review review = findById(reviewId).get();
        post.getReviews().remove(review);
        reviewRepository.delete(review);
    }

}
