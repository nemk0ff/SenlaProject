package ru.senla.socialnetwork.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import ru.senla.socialnetwork.model.users.Gender;
import ru.senla.socialnetwork.model.users.ProfileType;

@Schema(description = "Данные для регистрации нового пользователя")
public record RegisterDTO(
    @Schema(description = "Email", example = "example@senla.ru")
    @NotBlank(message = "email не должен быть пустым")
    String email,

    @Schema(description = "Пароль", example = "password123")
    @NotBlank(message = "пароль не должен быть пустым")
    String password,

    @Schema(description = "Имя", example = "Иван")
    @NotBlank(message = "введите ваше имя")
    String name,

    @Schema(description = "Фамилия", example = "Иванов")
    @NotBlank(message = "введите вашу фамилию")
    String surname,

    @Schema(description = "Дата рождения", example = "01-01-2000")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    LocalDate birthDate,

    @Schema(description = "Пол", example = "MALE")
    Gender gender,

    @Schema(description = "О себе", example = "Начинающий Java Developer, иду к мечте!")
    String aboutMe,

    @Schema(description = "Тип профиля", example = "OPEN")
    ProfileType profileType
) {
}