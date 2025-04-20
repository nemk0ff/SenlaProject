package ru.senla.socialnetwork.exceptions.users;

public class EmailAlreadyExistsException extends IllegalArgumentException {
  public EmailAlreadyExistsException(String email) {
    super(email);
  }
}
