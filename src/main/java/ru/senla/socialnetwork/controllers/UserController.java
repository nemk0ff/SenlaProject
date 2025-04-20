package ru.senla.socialnetwork.controllers;

import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.dto.users.ChangeEmailDTO;
import ru.senla.socialnetwork.dto.users.UserEditDTO;
import ru.senla.socialnetwork.model.enums.Gender;

public interface UserController {
  ResponseEntity<?> get(Long id);

  ResponseEntity<?> find(String name, String surname, Gender gender, LocalDate birthdate);

  ResponseEntity<?> edit(UserEditDTO editDTO);

  ResponseEntity<?> changeEmail(ChangeEmailDTO request);
}
