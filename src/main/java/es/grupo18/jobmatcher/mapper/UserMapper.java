package es.grupo18.jobmatcher.mapper;

import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.dto.UserDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);
    User toEntity(UserDTO userDTO);

    List<UserDTO> toDTOs(List<User> users);
    List<User> toEntities(List<UserDTO> dtos);
}
