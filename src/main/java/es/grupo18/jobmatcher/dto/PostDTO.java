package es.grupo18.jobmatcher.dto;

import java.util.List;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public record PostDTO(
        @NotNull Long id,
        @NotBlank @Size(max = 200) String title,
        @Size(max = 10000) String content,
        @NotNull LocalDateTime timestamp,
        @NotNull Long authorId,
        byte[] image,
        @Pattern(regexp = "^(image/png|image/jpeg)$") String imageContentType,
        @Size(min = 1, max = 30) @NotBlank @Pattern(regexp = "^[^&<>\"']+$") String authorName,
        List<ReviewDTO> reviews) {
}
