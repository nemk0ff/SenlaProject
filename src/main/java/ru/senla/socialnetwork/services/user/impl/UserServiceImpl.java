package ru.senla.socialnetwork.services.user.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.users.UserDao;
import ru.senla.socialnetwork.dto.users.UserEditDTO;
import ru.senla.socialnetwork.dto.mappers.UserMapper;
import ru.senla.socialnetwork.exceptions.users.EmailAlreadyExistsException;
import ru.senla.socialnetwork.exceptions.users.UserException;
import ru.senla.socialnetwork.exceptions.users.UserNotRegisteredException;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.Gender;
import ru.senla.socialnetwork.model.users.UserRole;
import ru.senla.socialnetwork.services.user.UserService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserDao userDao;

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
      throw new EntityNotFoundException("По вашему запросу не найдено пользователей");
    }
    return foundUsers;
  }

  @Transactional
  @Override
  public User edit(UserEditDTO editDTO) {
    User mergedUser = UserMapper.INSTANCE.userEditDTOtoUser(editDTO);
    User oldUser = getUserByEmail(editDTO.email());
    mergedUser.setId(oldUser.getId());
    return userDao.saveOrUpdate(mergedUser);
  }

  @Transactional
  @Override
  public User changeEmail(String oldEmail, String newEmail) {
    User user = getUserByEmail(oldEmail);
    if (oldEmail.equals(newEmail)) {
      throw new UserException("Старый и новый email овпадают");
    }
    if (existsByEmail(newEmail)) {
      throw new EmailAlreadyExistsException(newEmail);
    }
    user.setEmail(newEmail);
    return userDao.saveOrUpdate(user);
  }

  @Override
  @Transactional(readOnly = true)
  public User getUserByEmail(String email) {
    return userDao.findByEmail(email).orElseThrow(
        () -> new UserNotRegisteredException(email));
  }

  @Transactional
  @Override
  public boolean existsByEmail(String email) {
    return userDao.findByEmail(email).isPresent();
  }

  @Transactional
  @Override
  public boolean isAdmin(String email) {
    return getUserByEmail(email).getRole().equals(UserRole.ADMIN);
  }
}
