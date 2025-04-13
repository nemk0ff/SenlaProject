package ru.senla.socialnetwork.services;

import java.util.List;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.senla.socialnetwork.dto.AuthDTO;
import ru.senla.socialnetwork.dto.UserDTO;
import ru.senla.socialnetwork.model.entities.User;

public interface UserService extends UserDetailsService {
  boolean isUserValid(AuthDTO userInfo);

  String getRole(String username);

  User get(long userId);

  List<User> find(UserDTO userDTO);
  // TODO: Редактирование персональной информации пользователя

  // TODO: Поиск пользователей по: фамилии, имени, полу, возрасту
}
