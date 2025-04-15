package ru.senla.socialnetwork.services.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dto.AuthDTO;
import ru.senla.socialnetwork.dto.UserDTO;
import ru.senla.socialnetwork.dto.mappers.UserMapper;
import ru.senla.socialnetwork.exceptions.UserAlreadyExistsException;
import ru.senla.socialnetwork.exceptions.UserNotRegisteredException;
import ru.senla.socialnetwork.model.entities.User;
import ru.senla.socialnetwork.model.enums.UserRole;
import ru.senla.socialnetwork.repository.impl.UserDaoImpl;
import ru.senla.socialnetwork.services.UserService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserDaoImpl userDao;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional(readOnly = true)
  public User get(long userId) {
    return userDao.find(userId).orElseThrow(() -> new EntityNotFoundException(""));
  }

  @Override
  public List<User> find(UserDTO userDTO) {
    List<User> foundUsers = userDao.findByParam(UserMapper.INSTANCE.toUser(userDTO));
    if (foundUsers.isEmpty()) {
      throw new EntityNotFoundException("По вашему запросу не найдено пользователей.");
    }
    return foundUsers;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    log.debug("Поиск пользователя по email: {}", email);
    User user = userDao.findByEmail(email).orElseThrow(
        () -> new UserNotRegisteredException("Пользователь " + email + " не найден"));
    log.debug("Пользователь найден: {}", user);
    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getEmail())
        .password(user.getPassword())
        .roles(user.getRole().name())
        .build();
  }

  @Transactional
  @Override
  public boolean isUserValid(AuthDTO userInfo) {
    log.info("Проверяем логин и пароль пользователя {}...", userInfo.getEmail());
    UserDetails correctDetails = loadUserByUsername(userInfo.getEmail());
    if (passwordEncoder.matches(userInfo.getPassword(), correctDetails.getPassword())) {
      log.info("Пользователь ввёл корректные данные");
      return true;
    }
    log.info("Пользователь ввёл неверный пароль");
    return false;
  }

  @Transactional
  @Override
  public String getRole(String email) {
    log.info("Ищем роль пользователя {}...", email);
    String role = loadUserByUsername(email).getAuthorities().iterator().next().getAuthority();
    log.info("Роль для {} найдена: {}", email, role);
    return role;
  }

  @Transactional
  @Override
  public User create(UserDTO userDTO) {
    log.info("Регистрируем нового пользователя {}...", userDTO.getName());
    if (existsByEmail(userDTO.getEmail())) {
      throw new UserAlreadyExistsException("Пользователь " + userDTO.getEmail()
          + " уже существует");
    }
    User user = User.builder()
        .email(userDTO.getEmail())
        .password(passwordEncoder.encode(userDTO.getPassword()))
        .name(userDTO.getName())
        .surname(userDTO.getSurname())
        .role(UserRole.USER)
        .gender(userDTO.getGender())
        .build();
    save(user);
    log.info("Пользователь {} успешно зарегистрирован.", userDTO.getEmail());
    return user;
  }

  @Transactional
  @Override
  public boolean existsByEmail(String email) {
    return userDao.findByEmail(email).isPresent();
  }

  @Transactional
  @Override
  public void save(User user) {
    userDao.save(user);
  }

  @Transactional
  @Override
  public User edit(UserDTO userDTO) {
    User mergedUser = UserMapper.INSTANCE.toUser(userDTO);
    User oldUser = userDao.findByEmail(userDTO.getEmail())
        .orElseThrow(() -> new EntityNotFoundException(
            "Пользователь " + userDTO.getEmail() + " не зарегистрирован"));
    mergedUser.setId(oldUser.getId());
    return userDao.update(mergedUser);
  }
}
