package ru.senla.socialnetwork.controllers;

import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.dto.auth.AuthRequestDTO;
import ru.senla.socialnetwork.dto.auth.RegisterDTO;

public interface AuthController {
  ResponseEntity<?> register(RegisterDTO registerDTO);

  ResponseEntity<?> login(AuthRequestDTO request);
}
