package ru.senla.socialnetwork.dto.users;

import java.time.LocalDate;
import ru.senla.socialnetwork.model.users.Gender;
import ru.senla.socialnetwork.model.users.ProfileType;

public record UserRequestDTO(
    String name,
    String surname,
    LocalDate birthDate,
    Gender gender,
    ProfileType profileType,
    String aboutMe
) {
}
