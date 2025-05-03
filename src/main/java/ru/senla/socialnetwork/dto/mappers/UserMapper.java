package ru.senla.socialnetwork.dto.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.senla.socialnetwork.dto.auth.RegisterDTO;
import ru.senla.socialnetwork.dto.users.UserDTO;
import ru.senla.socialnetwork.dto.users.UserEditDTO;
import ru.senla.socialnetwork.model.users.User;

@Mapper
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  UserDTO toUserResponseDTO(User user);

  List<UserDTO> toListUserResponseDTO(List<User> users);

  User registrationDTOtoUser(RegisterDTO registrationDTO);

  User userEditDTOtoUser(UserEditDTO userEditDTO);
}
