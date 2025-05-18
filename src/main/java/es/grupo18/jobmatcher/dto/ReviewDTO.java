package es.grupo18.jobmatcher.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ReviewDTO(
        @NotNull Long id,
        @Size(min = 1, max = 10000) @NotBlank @Pattern(regexp = "^[^&<>\"']+$") String text,
        @Min(value = 0) @Max(value = 5) int rating,
        @NotNull Long authorId,
        @NotNull Long postId,
        @Size(min = 1, max = 30) @NotBlank @Pattern(regexp = "^[^&<>\"']+$") String authorName) {
}
