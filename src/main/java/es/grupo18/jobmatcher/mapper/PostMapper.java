package es.grupo18.jobmatcher.mapper;

import es.grupo18.jobmatcher.model.Post;
import es.grupo18.jobmatcher.dto.PostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "timestamp", target = "timestamp", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    PostDTO toDTO(Post post);

    @Mapping(target = "author.name", ignore = true)
    @Mapping(source = "authorId", target = "author.id")
    Post toDomain(PostDTO dto);

    List<PostDTO> toDTOs(List<Post> posts);
    List<Post> toDomains(List<PostDTO> dtos);
}
