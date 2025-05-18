package ru.senla.socialnetwork.dto.users;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import ru.senla.socialnetwork.model.users.Gender;
import ru.senla.socialnetwork.model.users.ProfileType;

public record UserRequestDTO(
    @NotBlank String name,
    @NotBlank String surname,
    LocalDate birthDate,
    Gender gender,
    ProfileType profileType,
    String aboutMe
) {
}
