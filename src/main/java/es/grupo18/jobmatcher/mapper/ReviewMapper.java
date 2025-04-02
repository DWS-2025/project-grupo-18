package es.grupo18.jobmatcher.mapper;

import es.grupo18.jobmatcher.model.Review;
import es.grupo18.jobmatcher.dto.ReviewDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "post.id", target = "postId")
    ReviewDTO toDTO(Review review);

    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "postId", target = "post.id")
    Review toDomain(ReviewDTO dto);

    List<ReviewDTO> toDTOs(List<Review> reviews);
    List<Review> toDomains(List<ReviewDTO> dtos);
}
