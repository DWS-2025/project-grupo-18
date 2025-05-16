package es.grupo18.jobmatcher.dto;

public record ReviewDTO(
        Long id,
        String text,
        int rating,
        Long authorId,
        Long postId,
        String authorName) {
}
