package ru.senla.socialnetwork.dto.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.senla.socialnetwork.dto.UserDTO;
import ru.senla.socialnetwork.model.entities.User;

@Mapper
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  UserDTO toDTO(User user);

  List<UserDTO> toListDTO(List<User> users);

  User toUser(UserDTO userDTO);
}
