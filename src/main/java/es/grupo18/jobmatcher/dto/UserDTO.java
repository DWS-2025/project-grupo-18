package es.grupo18.jobmatcher.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserDTO(
        Long id,
        @Pattern(regexp = "^[^&<>\"]+$", message = "El nombre no puede contener caracteres especiales") String name,
        @NotBlank @Email(message = "El correo debe ser válido y contener un @") String email,
        String phone,
        String location,
        String bio,
        int experience,
        byte[] image,
        String imageContentType,
        List<String> roles,
        String cvFileName) {
}
