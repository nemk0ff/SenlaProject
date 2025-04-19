package ru.senla.socialnetwork.services.impl;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dto.users.UserEditDTO;
import ru.senla.socialnetwork.dto.mappers.UserMapper;
import ru.senla.socialnetwork.exceptions.users.EmailAlreadyExistsException;
import ru.senla.socialnetwork.exceptions.general.EntitiesNotFoundException;
import ru.senla.socialnetwork.exceptions.users.UserNotRegisteredException;
import ru.senla.socialnetwork.model.entities.User;
import ru.senla.socialnetwork.model.enums.Gender;
import ru.senla.socialnetwork.repository.impl.UserDaoImpl;
import ru.senla.socialnetwork.services.UserService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserDaoImpl userDao;

  @Override
  @Transactional(readOnly = true)
  public User get(long userId) {
    return userDao.find(userId).orElseThrow(
        () -> new UserNotRegisteredException("id" + userId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> find(String name, String surname, Gender gender, LocalDate birthdate) {
    List<User> foundUsers = userDao.findByParam(name, surname, gender, birthdate);
    if (foundUsers.isEmpty()) {
      throw new EntitiesNotFoundException();
    }
    return foundUsers;
  }

  @Transactional
  @Override
  public boolean existsByEmail(String email) {
    return userDao.findByEmail(email).isPresent();
  }

  @Transactional
  @Override
  public User edit(UserEditDTO editDTO) {
    User mergedUser = UserMapper.INSTANCE.userEditDTOtoUser(editDTO);
    User oldUser = userDao.findByEmail(editDTO.email()).orElseThrow(
            () -> new UserNotRegisteredException(editDTO.email()));
    mergedUser.setId(oldUser.getId());
    return userDao.update(mergedUser);
  }

  @Transactional
  @Override
  public User changeEmail(String oldEmail, String newEmail) {
    User user = userDao.findByEmail(oldEmail).orElseThrow(
        () -> new UserNotRegisteredException(oldEmail));
    if (oldEmail.equals(newEmail)) {
      throw new IllegalArgumentException("Старый и новый email овпадают");
    }
    if (existsByEmail(newEmail)) {
      throw new EmailAlreadyExistsException(newEmail);
    }
    user.setEmail(newEmail);
    return userDao.update(user);
  }
}
