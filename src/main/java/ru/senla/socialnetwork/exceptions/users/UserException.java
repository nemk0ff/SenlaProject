package ru.senla.socialnetwork.exceptions.users;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
  private final String action;

  public UserException(String message, String action) {
    super(message);
    this.action = action;
  }

  public UserException(String message) {
    super(message);
    action = "Ошибка при действии с пользователем";
  }
}
