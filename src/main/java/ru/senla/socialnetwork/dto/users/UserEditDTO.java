package ru.senla.socialnetwork.dto.users;

import java.time.LocalDate;
import ru.senla.socialnetwork.model.users.Gender;

public record UserEditDTO(
    String email,
    String name,
    String surname,
    LocalDate birthDate,
    Gender gender,
    String aboutMe
) {}
