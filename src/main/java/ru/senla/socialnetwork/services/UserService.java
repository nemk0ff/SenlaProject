package ru.senla.socialnetwork.services;

import java.util.List;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.senla.socialnetwork.dto.AuthDTO;
import ru.senla.socialnetwork.dto.UserDTO;
import ru.senla.socialnetwork.model.entities.User;

public interface UserService extends UserDetailsService {
  boolean isUserValid(AuthDTO userInfo);

  String getRole(String username);

  User create(UserDTO userDTO);

  User get(long userId);

  boolean existsByEmail(String email);

  void save(User user);

  List<User> find(UserDTO userDTO);

  User edit(UserDTO userDTO);

  User changeEmail(String oldEmail, String newEmail);
}
