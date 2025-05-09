package ru.senla.socialnetwork.controllers.users.impl;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.users.UserController;
import ru.senla.socialnetwork.dto.users.UserResponseDTO;
import ru.senla.socialnetwork.dto.users.UserRequestDTO;
import ru.senla.socialnetwork.dto.mappers.UserMapper;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.Gender;
import ru.senla.socialnetwork.security.JwtUtils;
import ru.senla.socialnetwork.services.user.UserService;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
@RequestMapping("/users")
public class UserControllerImpl implements UserController {
  private final UserService userService;

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable("id") Long id) {
    log.info("Запрос на получение пользователя с id: {}", id);
    UserResponseDTO response = UserMapper.INSTANCE.toUserResponseDTO(userService.get(id));
    log.info("Найден пользователь ID: {}, email: {}", id, response.email());
    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping("/find")
  public ResponseEntity<?> find(
      @RequestParam @Nullable String name,
      @RequestParam @Nullable String surname,
      @RequestParam @Nullable Gender gender,
      @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      LocalDate birthdate) {
    log.info("Поиск пользователей по параметрам: name={}, surname={}, gender={}, birthdate={}",
        name, surname, gender, birthdate);
    List<UserResponseDTO> users = UserMapper.INSTANCE.toListUserResponseDTO(
        userService.find(name, surname, gender, birthdate));
    log.info("Найдено {} пользователей по заданным критериям", users.size());
    return ResponseEntity.ok(users);
  }

  @Override
  @PutMapping("/edit")
  @PreAuthorize("#editDTO.email == authentication.name")
  public ResponseEntity<?> edit(@RequestBody UserRequestDTO editDTO) {
    log.info("Запрос на редактирование пользователя: {}", editDTO.email());
    UserResponseDTO updatedUser = UserMapper.INSTANCE.toUserResponseDTO(userService.edit(editDTO));
    log.info("Пользователь {} успешно обновлен", updatedUser.email());
    return ResponseEntity.ok(updatedUser);
  }

  @Override
  @PostMapping("/change-email")
  public ResponseEntity<?> changeEmail(
      @RequestParam @Email String newEmail,
      Authentication auth) {
    log.info("Смена email с {} на {}", auth.getName(), newEmail);

    User updatedUser = userService.changeEmail(auth.getName(), newEmail);
    String newToken = JwtUtils.generateToken(newEmail, updatedUser.getRole().toString());

    log.info("Email успешно изменен. Новый JWT токен сгенерирован для {}", newEmail);
    log.debug("Старый email: {}, Новый email: {}", auth.getName(), newEmail);
    return ResponseEntity.ok(Map.of(
        "token", newToken,
        "updatedUser", UserMapper.INSTANCE.toUserResponseDTO(updatedUser)
    ));
  }
}
