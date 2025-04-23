package ru.senla.socialnetwork.services.general;

import ru.senla.socialnetwork.model.users.User;

public interface CommonService {
  User getUserByEmail(String email);

  boolean existsByEmail(String email);
}
