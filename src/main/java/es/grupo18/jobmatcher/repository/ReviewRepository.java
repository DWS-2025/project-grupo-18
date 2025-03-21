package es.grupo18.jobmatcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.grupo18.jobmatcher.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
}
