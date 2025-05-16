package es.grupo18.jobmatcher.dto;

public record CompanyDTO(
                Long id,
                String name,
                String email,
                String location,
                String bio) {
}
