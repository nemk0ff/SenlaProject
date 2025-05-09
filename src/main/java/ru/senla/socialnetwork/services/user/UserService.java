package ru.senla.socialnetwork.services.user;

import java.time.LocalDate;
import java.util.List;
import ru.senla.socialnetwork.dto.users.UserRequestDTO;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.Gender;

public interface UserService {
  User get(long userId);

  List<User> find(String name, String surname, Gender gender, LocalDate birthdate);

  User edit(UserRequestDTO editDTO);

  User changeEmail(String oldEmail, String newEmail);

  User getUserByEmail(String email);

  boolean existsByEmail(String email);

  boolean isAdmin(String email);
}
