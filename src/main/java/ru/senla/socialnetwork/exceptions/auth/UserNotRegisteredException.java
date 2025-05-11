package ru.senla.socialnetwork.exceptions.auth;

import org.springframework.security.core.AuthenticationException;

public class UserNotRegisteredException extends AuthenticationException {
  public UserNotRegisteredException(String idOrEmail) {
    super("User " + idOrEmail + " is not registered in social network");
  }
}
