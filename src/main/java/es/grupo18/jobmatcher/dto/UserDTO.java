package es.grupo18.jobmatcher.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public record UserDTO(
                @NotNull Long id,
                @Size(min = 1, max = 30) @NotBlank @Pattern(regexp = "^[^&<>\"']+$") String name,
                @Size(min = 1, max = 50) @NotBlank @Email() String email,
                @Size(min = 1, max = 15) @Pattern(regexp = "^\\+?[0-9 ]{9,15}$") String phone,
                @Size(max = 50) String location,
                @Size(max = 300) String bio,
                @Min(value = 0) @Max(value = 99) int experience,
                byte[] image,
                @Pattern(regexp = "^(image/png|image/jpeg)$") String imageContentType,
                List<String> roles,
                @Pattern(regexp = "^[a-zA-Z0-9_.-]+\\.pdf$") @Size(max = 50) String cvFileName) {
}
