package ru.senla.socialnetwork.exceptions.users;

import org.springframework.security.core.AuthenticationException;

public class UserNotRegisteredException extends AuthenticationException {
  public UserNotRegisteredException(String idOrEmail) {
    super("Пользователь " + idOrEmail + " не зарегистрирован");
  }
}
