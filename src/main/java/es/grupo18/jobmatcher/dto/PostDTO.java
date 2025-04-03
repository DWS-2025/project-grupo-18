package es.grupo18.jobmatcher.dto;

import java.util.List;

public record PostDTO(
        Long id,
        String title,
        String content,
        String timestamp,
        Long authorId,
        byte[] image,
        String imageContentType,
        String authorName,
        List<ReviewDTO> reviews // 👈 AÑADIR ESTO
) {}
