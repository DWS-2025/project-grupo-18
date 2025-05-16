package es.grupo18.jobmatcher.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.grupo18.jobmatcher.dto.ReviewDTO;
import es.grupo18.jobmatcher.model.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "author.name", target = "authorName")
    ReviewDTO toDTO(Review review);

    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "postId", target = "post.id")
    @Mapping(target = "author.name", ignore = true)
    Review toDomain(ReviewDTO dto);

    List<ReviewDTO> toDTOs(List<Review> reviews);

    List<Review> toDomains(List<ReviewDTO> dtos);
    
}
