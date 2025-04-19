package ru.senla.socialnetwork.exceptions.users;

import org.springframework.security.core.AuthenticationException;

public class EmailAlreadyExistsException extends AuthenticationException {
  public EmailAlreadyExistsException(String message) {
    super("Email " + message + " уже зарегистрирован.");
  }
}
