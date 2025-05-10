package ru.senla.socialnetwork.services.user.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.users.UserDao;
import ru.senla.socialnetwork.dto.users.UserRequestDTO;
import ru.senla.socialnetwork.exceptions.users.EmailAlreadyExistsException;
import ru.senla.socialnetwork.exceptions.users.UserException;
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
        () -> new EntityNotFoundException("id" + userId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> find(String name, String surname, Gender gender, LocalDate birthdate) {
    return userDao.findByParam(name, surname, gender, birthdate);
  }

  @Transactional
  @Override
  public User edit(UserRequestDTO editDTO, String clientEmail) {
    User user = getUserByEmail(clientEmail);

    Optional.ofNullable(editDTO.name()).ifPresent(user::setName);
    Optional.ofNullable(editDTO.surname()).ifPresent(user::setSurname);
    Optional.ofNullable(editDTO.birthDate()).ifPresent(user::setBirthDate);
    Optional.ofNullable(editDTO.gender()).ifPresent(user::setGender);
    Optional.ofNullable(editDTO.profileType()).ifPresent(user::setProfileType);
    Optional.ofNullable(editDTO.aboutMe()).ifPresent(user::setAboutMe);

    return userDao.saveOrUpdate(user);
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
        () -> new EntityNotFoundException(email));
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
