    package es.grupo18.jobmatcher.dto;

    public record PostDTO(
    Long id,
    String title,
    String content,
    String timestamp,
    Long authorId,
    byte[] image,
    String imageContentType,
    String authorName
) {}

