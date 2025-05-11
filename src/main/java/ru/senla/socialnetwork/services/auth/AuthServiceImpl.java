package ru.senla.socialnetwork.services.auth;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.users.UserDao;
import ru.senla.socialnetwork.dto.auth.AuthRequestDTO;
import ru.senla.socialnetwork.dto.auth.AuthResponseDTO;
import ru.senla.socialnetwork.dto.auth.RegisterDTO;
import ru.senla.socialnetwork.dto.mappers.UserMapper;
import ru.senla.socialnetwork.dto.users.UserResponseDTO;
import ru.senla.socialnetwork.exceptions.auth.IllegalPasswordException;
import ru.senla.socialnetwork.exceptions.users.EmailAlreadyExistsException;
import ru.senla.socialnetwork.exceptions.auth.UserNotRegisteredException;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.UserRole;
import ru.senla.socialnetwork.security.JwtUtils;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final UserDao userDao;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  @Override
  public AuthResponseDTO getAuthResponse(AuthRequestDTO requestDTO) {
    log.info("Проверяем логин и пароль пользователя {}...", requestDTO.email());
    UserDetails correctDetails = loadUserByUsername(requestDTO.email());

    if (!passwordEncoder.matches(requestDTO.password(), correctDetails.getPassword())) {
      throw new IllegalPasswordException();
    }

    log.info("Пользователь ввёл верный пароль, получаем его роль и генерируем токен...");
    String role = loadUserByUsername(requestDTO.email())
        .getAuthorities().iterator().next().getAuthority();
    String token = JwtUtils.generateToken(requestDTO.email(), role);
    log.info("Токен сгенерирован успешно.");
    return new AuthResponseDTO(role, token);
  }

  @Transactional
  @Override
  public UserResponseDTO register(RegisterDTO regDTO) {
    log.info("Регистрируем нового пользователя {}...", regDTO.name());
    if (userDao.findByEmail(regDTO.email()).isPresent()) {
      throw new EmailAlreadyExistsException(regDTO.email());
    }
    User user = UserMapper.INSTANCE.toUser(regDTO);
    user.setPassword(passwordEncoder.encode(regDTO.password()));
    user.setRole(UserRole.USER);

    userDao.saveOrUpdate(user);
    log.info("Пользователь {} успешно зарегистрирован.", regDTO.email());
    return UserMapper.INSTANCE.toUserResponseDTO(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    log.debug("Поиск пользователя по email: {}", email);
    User user = userDao.findByEmail(email).orElseThrow(
        () -> new UserNotRegisteredException(email));

    log.debug("Пользователь найден: {}", user);
    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getEmail())
        .password(user.getPassword())
        .roles(user.getRole().name())
        .build();
  }
}
