package ru.senla.socialnetwork.exceptions.users;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthException extends AuthenticationException {
  public JwtAuthException(String message) {
    super(message);
  }
}
