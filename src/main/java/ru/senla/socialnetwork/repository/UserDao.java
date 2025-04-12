package ru.senla.socialnetwork.repository;

import java.util.Optional;
import ru.senla.socialnetwork.model.entities.User;

public interface UserDao {
  Optional<User> get(long userId);


}
