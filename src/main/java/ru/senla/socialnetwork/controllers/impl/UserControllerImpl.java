package ru.senla.socialnetwork.controllers.impl;

import jakarta.validation.Valid;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.UserController;
import ru.senla.socialnetwork.dto.AuthDTO;
import ru.senla.socialnetwork.dto.ChangeEmailDTO;
import ru.senla.socialnetwork.dto.UserDTO;
import ru.senla.socialnetwork.dto.mappers.UserMapper;
import ru.senla.socialnetwork.model.entities.User;
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
    return ResponseEntity.ok(userService.get(id));
  }

  @Override
  @GetMapping("/find")
  public ResponseEntity<?> find(@RequestBody UserDTO userDTO) {
    return ResponseEntity.ok(UserMapper.INSTANCE.toListDTO(userService.find(userDTO)));
  }

  @Override
  @GetMapping("/edit")
  @PreAuthorize("hasRole('ADMIN') or #userDTO.email == authentication.name")
  public ResponseEntity<?> edit(@RequestBody UserDTO userDTO) {
    return ResponseEntity.ok(UserMapper.INSTANCE.toDTO(userService.edit(userDTO)));
  }

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
        "updatedUser", UserMapper.INSTANCE.toDTO(updatedUser)));
  }

  @PostMapping("/login")
  @Override
  public ResponseEntity<?> login(@RequestBody @Valid AuthDTO request) {
    if (userService.isUserValid(request)) {
      String role = userService.getRole(request.getEmail());
      String token = JwtUtils.generateToken(request.getEmail(), role);
      return ResponseEntity.ok(Map.of("token", token, "role", role));
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(Map.of("error", "Неверный логин или пароль"));
  }

  @PostMapping("/register")
  @Override
  public ResponseEntity<?> register(@RequestBody @Valid UserDTO userDTO) {
    return ResponseEntity.ok(UserMapper.INSTANCE.toDTO(userService.create(userDTO)));
  }
}
