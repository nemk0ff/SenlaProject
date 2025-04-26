package ru.senla.socialnetwork.exceptions.auth;

import org.springframework.security.core.AuthenticationException;

public class IllegalPasswordException extends AuthenticationException {
  public IllegalPasswordException() {
    super("Вы ввели неверный пароль");
  }
}
