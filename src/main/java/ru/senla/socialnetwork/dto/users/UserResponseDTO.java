package ru.senla.socialnetwork.dto.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import ru.senla.socialnetwork.model.users.Gender;
import ru.senla.socialnetwork.model.users.ProfileType;
import ru.senla.socialnetwork.model.users.UserRole;

@Schema(description = "Данные зарегистрированного пользователя")
public record UserResponseDTO(
    @Schema(description = "ID пользователя", example = "123")
    Long id,

    @Schema(description = "Email", example = "example@senla.ru")
    String email,

    @Schema(description = "Роль", example = "USER")
    UserRole role,

    @Schema(description = "Имя", example = "Иван")
    String name,

    @Schema(description = "Фамилия", example = "Иванов")
    String surname,

    @Schema(description = "Дата рождения", example = "01-01-2000")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    LocalDate birthDate,

    @Schema(description = "Пол", example = "MALE")
    Gender gender,

    @Schema(description = "О себе", example = "Начинающий Java Developer, иду к мечте!")
    String aboutMe,

    @Schema(description = "Тип профиля", example = "OPEN")
    ProfileType profileType,

    @Schema(description = "Дата регистрации", example = "2025-05-020T12:00:00+03:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime registeredAt
) {
}