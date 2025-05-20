package ru.senla.socialnetwork.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import ru.senla.socialnetwork.model.users.Gender;
import ru.senla.socialnetwork.model.users.ProfileType;

@Schema(description = "DTO для обновления данных пользователя")
public record UserRequestDTO(
    @Schema(description = "Имя пользователя", example = "Иван", required = true)
    @NotBlank String name,

    @Schema(description = "Фамилия пользователя", example = "Иванов", required = true)
    @NotBlank String surname,

    @Schema(description = "Дата рождения", example = "2000-01-01")
    LocalDate birthDate,

    @Schema(description = "Пол пользователя", implementation = Gender.class)
    Gender gender,

    @Schema(description = "Тип профиля", implementation = ProfileType.class)
    ProfileType profileType,

    @Schema(description = "Информация о пользователе", example = "Люблю путешествовать")
    String aboutMe
) {
}

