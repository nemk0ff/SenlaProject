package ru.senla.socialnetwork.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UserNotRegisteredException extends AuthenticationException {
  public UserNotRegisteredException(String message) {
    super(message);
  }
}
