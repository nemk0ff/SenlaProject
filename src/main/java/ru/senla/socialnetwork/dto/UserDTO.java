package ru.senla.socialnetwork.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.senla.socialnetwork.model.enums.Gender;
import ru.senla.socialnetwork.model.enums.UserRole;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {
  private Long id;

  private String email;

  private UserRole role;

  private String password;

  private String firstName;

  private String lastName;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
  private LocalDate birthDate;

  private Gender gender;

  private String aboutMe;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss XXX")
  private ZonedDateTime registeredAt;
}
