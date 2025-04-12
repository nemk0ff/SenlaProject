package ru.senla.socialnetwork.controllers;

import org.springframework.http.ResponseEntity;

public interface UserController {
  ResponseEntity<?> get(Long id);
}
