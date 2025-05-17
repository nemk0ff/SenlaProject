package ru.senla.socialnetwork.dto.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.senla.socialnetwork.dto.auth.RegisterDTO;
import ru.senla.socialnetwork.dto.users.UserResponseDTO;
import ru.senla.socialnetwork.model.users.User;

@Mapper
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  UserResponseDTO toUserResponseDTO(User user);

  List<UserResponseDTO> toListUserResponseDTO(List<User> users);

  User toUser(RegisterDTO registrationDTO);
}