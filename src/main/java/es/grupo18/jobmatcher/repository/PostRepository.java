package es.grupo18.jobmatcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.grupo18.jobmatcher.model.Post;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE " +
            "(:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :title, '%')))")
    List<Post> searchPosts(@Param("title") String title);

}
