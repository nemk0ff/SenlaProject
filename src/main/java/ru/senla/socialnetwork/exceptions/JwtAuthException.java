package ru.senla.socialnetwork.exceptions;

public class JwtAuthException extends RuntimeException {
  public JwtAuthException(String message) {
    super(message);
  }
}
