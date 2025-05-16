package ru.senla.socialnetwork.controllers.auth;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.dto.auth.AuthRequestDTO;
import ru.senla.socialnetwork.dto.auth.RegisterDTO;

public interface AuthController {
  ResponseEntity<?> register(@Valid RegisterDTO registerDTO);

  ResponseEntity<?> login(@Valid AuthRequestDTO request);
}
