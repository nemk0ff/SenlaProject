package ru.senla.socialnetwork.controllers.users;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.users.UserRequestDTO;
import ru.senla.socialnetwork.model.users.Gender;

public interface UserController {
  ResponseEntity<?> get(Long id);

  ResponseEntity<?> get(String email);

  ResponseEntity<?> find(String name, String surname, Gender gender, LocalDate birthdate);

  ResponseEntity<?> edit(@Valid UserRequestDTO editDTO, Authentication auth);

  ResponseEntity<?> changeEmail(@NotBlank String newEmail, Authentication auth);
}
