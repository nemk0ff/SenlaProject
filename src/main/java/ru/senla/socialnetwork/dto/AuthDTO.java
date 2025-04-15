package ru.senla.socialnetwork.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthDTO {
  @NotBlank(message = "Почта (имя пользователя) не может быть пустой")
  private String mail;
  @NotBlank(message = "Пароль не может быть пустым")
  private String password;
}
