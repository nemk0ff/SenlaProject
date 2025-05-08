package ru.senla.socialnetwork.controllers.auth;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.dto.auth.AuthRequestDTO;
import ru.senla.socialnetwork.dto.auth.AuthResponseDTO;
import ru.senla.socialnetwork.dto.auth.RegisterDTO;
import ru.senla.socialnetwork.dto.users.UserDTO;
import ru.senla.socialnetwork.services.auth.AuthService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthControllerImpl implements AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  @Override
  public ResponseEntity<?> login(@RequestBody @Valid AuthRequestDTO request) {
    log.info("Попытка входа пользователя с email: {}...", request.email());
    AuthResponseDTO response = authService.getAuthResponse(request);
    log.info("Успешный вход пользователя с email: {}.", request.email());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/register")
  @Override
  public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO registerDTO) {
    log.info("Попытка регистрации нового пользователя с email: {}...", registerDTO.email());
    UserDTO response = authService.register(registerDTO);
    log.info("Успешная регистрация пользователя с email: {}", registerDTO.email());
    return ResponseEntity.ok(response);
  }
}
