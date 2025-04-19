package ru.senla.socialnetwork.services;

import java.time.LocalDate;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.senla.socialnetwork.dto.auth.AuthRequestDTO;
import ru.senla.socialnetwork.dto.auth.AuthResponseDTO;
import ru.senla.socialnetwork.dto.auth.RegisterDTO;
import ru.senla.socialnetwork.dto.users.UserEditDTO;
import ru.senla.socialnetwork.model.entities.User;
import ru.senla.socialnetwork.model.enums.Gender;

public interface UserService {
  User get(long userId);

  boolean existsByEmail(String email);

  List<User> find(String name, String surname, Gender gender, LocalDate birthdate);

  User edit(UserEditDTO editDTO);

  User changeEmail(String oldEmail, String newEmail);
}
