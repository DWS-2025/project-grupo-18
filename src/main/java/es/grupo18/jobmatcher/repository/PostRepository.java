package es.grupo18.jobmatcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.grupo18.jobmatcher.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    
}
