package ru.senla.socialnetwork.dao.users;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import ru.senla.socialnetwork.dao.GenericDao;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.Gender;

public interface UserDao extends GenericDao<User> {
  List<User> findByParam(String name, String surname, Gender gender, LocalDate birthdate);

  Optional<User> findByEmail(String email);
}
