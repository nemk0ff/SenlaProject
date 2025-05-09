package ru.senla.socialnetwork.controllers.users;

import jakarta.validation.constraints.Email;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.users.UserRequestDTO;
import ru.senla.socialnetwork.model.users.Gender;

public interface UserController {
  ResponseEntity<?> get(Long id);

  ResponseEntity<?> find(String name, String surname, Gender gender, LocalDate birthdate);

  ResponseEntity<?> edit(UserRequestDTO editDTO);

  ResponseEntity<?> changeEmail(@Email String newEmail, Authentication auth);
}
