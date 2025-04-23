package ru.senla.socialnetwork.controllers.impl;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.UserController;
import ru.senla.socialnetwork.dto.users.ChangeEmailDTO;
import ru.senla.socialnetwork.dto.users.UserEditDTO;
import ru.senla.socialnetwork.dto.mappers.UserMapper;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.Gender;
import ru.senla.socialnetwork.security.JwtUtils;
import ru.senla.socialnetwork.services.UserService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserControllerImpl implements UserController {
  private final UserService userService;

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable("id") Long id) {
    return ResponseEntity.ok(UserMapper.INSTANCE.toUserResponseDTO(userService.get(id)));
  }

  @Override
  @GetMapping("/find")
  public ResponseEntity<?> find(@RequestParam @Nullable String name,
                                @RequestParam @Nullable String surname,
                                @RequestParam @Nullable Gender gender,
                                @RequestParam @Nullable
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                LocalDate birthdate) {
    return ResponseEntity.ok(UserMapper.INSTANCE.toListUserResponseDTO(
        userService.find(name, surname, gender, birthdate)));
  }

  @Override
  @GetMapping("/edit")
  @PreAuthorize("hasRole('ADMIN') or #editDTO.email == authentication.name")
  public ResponseEntity<?> edit(@RequestBody UserEditDTO editDTO) {
    return ResponseEntity.ok(UserMapper.INSTANCE.toUserResponseDTO(userService.edit(editDTO)));
  }

  @Override
  @PostMapping("/change-email")
  @PreAuthorize("hasRole('ADMIN') or #request.currentEmail == authentication.name")
  public ResponseEntity<?> changeEmail(@RequestBody @Valid ChangeEmailDTO request) {
    User updatedUser = userService.changeEmail(
        request.currentEmail(),
        request.newEmail());
    String newToken = JwtUtils.generateToken(
        request.newEmail(),
        updatedUser.getRole().toString());
    return ResponseEntity.ok(Map.of(
        "token", newToken,
        "updatedUser", UserMapper.INSTANCE.toUserResponseDTO(updatedUser)));
  }
}
