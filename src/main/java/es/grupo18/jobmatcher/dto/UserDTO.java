package es.grupo18.jobmatcher.dto;

import java.util.List;

public record UserDTO(
                Long id,
                String name,
                String email,
                String phone,
                String location,
                String bio,
                int experience,
                byte[] image,
                String imageContentType,
                List<String> roles,
                String cvFileName) {
}
