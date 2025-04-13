package ru.senla.socialnetwork.controllers;

import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.dto.AuthDTO;
import ru.senla.socialnetwork.dto.UserDTO;

public interface UserController {
  ResponseEntity<?> get(Long id);

  ResponseEntity<?> find(UserDTO userDTO);

  ResponseEntity<?> register(UserDTO request);

  ResponseEntity<?> login(AuthDTO request);
}
