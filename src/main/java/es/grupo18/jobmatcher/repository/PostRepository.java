package es.grupo18.jobmatcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.grupo18.jobmatcher.model.Post;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE " +
       "(:query IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Post> searchPosts(@Param("query") String query);


}
