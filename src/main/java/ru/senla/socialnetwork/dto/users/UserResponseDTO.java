package ru.senla.socialnetwork.dto.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import ru.senla.socialnetwork.model.users.Gender;
import ru.senla.socialnetwork.model.users.UserRole;

public record UserResponseDTO(
    Long id,
    @NotBlank(message = "Укажите email пользователя")
    String email,
    UserRole role,
    String password,
    String name,
    String surname,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    LocalDate birthDate,
    Gender gender,
    String aboutMe,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss XXX")
    ZonedDateTime registeredAt
) {
}
