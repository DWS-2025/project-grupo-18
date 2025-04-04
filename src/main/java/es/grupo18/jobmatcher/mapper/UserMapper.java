package es.grupo18.jobmatcher.mapper;

import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "image", source = "image")
    @Mapping(target = "imageContentType", source = "imageContentType")
    UserDTO toDTO(User user);

    @Mapping(target = "image", source = "image")
    @Mapping(target = "imageContentType", source = "imageContentType")
    User toDomain(UserDTO userDTO);

    List<UserDTO> toDTOs(List<User> users);

    List<User> toDomains(List<UserDTO> dtos);

}
