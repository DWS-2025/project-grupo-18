package es.grupo18.jobmatcher.dto;

import java.util.List;
import java.time.LocalDateTime;

public record PostDTO(
        Long id,
        String title,
        String content,
        LocalDateTime timestamp,
        Long authorId,
        byte[] image,
        String imageContentType,
        String authorName,
        List<ReviewDTO> reviews
) {}
