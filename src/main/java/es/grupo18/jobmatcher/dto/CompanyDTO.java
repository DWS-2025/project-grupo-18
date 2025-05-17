package es.grupo18.jobmatcher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CompanyDTO(
        @NotNull Long id,
        @Size(min = 1, max = 30) @NotBlank @Pattern(regexp = "^[^&<>\"']+$") String name,
        @Size(min = 1, max = 50) @NotBlank @Email() String email,
        @Size(max = 50) String location,
        @Size(max = 300) String bio) {
}
