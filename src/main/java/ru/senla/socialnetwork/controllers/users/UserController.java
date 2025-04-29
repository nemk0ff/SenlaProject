package ru.senla.socialnetwork.controllers.users;

import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.dto.users.ChangeEmailDTO;
import ru.senla.socialnetwork.dto.users.UserEditDTO;
import ru.senla.socialnetwork.model.users.Gender;

public interface UserController {
  ResponseEntity<?> get(Long id);

  ResponseEntity<?> find(String name, String surname, Gender gender, LocalDate birthdate);

  ResponseEntity<?> edit(UserEditDTO editDTO);

  ResponseEntity<?> changeEmail(ChangeEmailDTO request);
}
