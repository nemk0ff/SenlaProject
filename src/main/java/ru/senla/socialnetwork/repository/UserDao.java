package ru.senla.socialnetwork.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import ru.senla.socialnetwork.model.entities.users.User;
import ru.senla.socialnetwork.model.enums.Gender;

public interface UserDao {
  List<User> findByParam(String name, String surname, Gender gender, LocalDate birthdate);

  Optional<User> findByEmail(String email);

  void save(User user);
}
