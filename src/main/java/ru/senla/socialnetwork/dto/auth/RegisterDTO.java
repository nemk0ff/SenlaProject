package ru.senla.socialnetwork.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import ru.senla.socialnetwork.model.users.Gender;

public record RegisterDTO(
    @NotBlank (message = "email не должен быть пустым")
    String email,
    @NotBlank (message = "пароль не должен быть пустым")
    String password,
    @NotBlank (message = "введите ваше имя")
    String name,
    @NotBlank (message = "введите вашу фамилию")
    String surname,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    LocalDate birthDate,
    Gender gender,
    String aboutMe
) {}
