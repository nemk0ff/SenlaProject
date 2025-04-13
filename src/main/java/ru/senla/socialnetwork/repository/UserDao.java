package ru.senla.socialnetwork.repository;

import java.util.List;
import java.util.Optional;
import ru.senla.socialnetwork.model.entities.User;

public interface UserDao {
  List<User> findByParam(User user);

  Optional<User> findByEmail(String email);
}
