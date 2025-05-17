package ru.senla.socialnetwork.exceptions.users;

public class EmailAlreadyExistsException extends UserException {
  public EmailAlreadyExistsException(String email) {
    super(email + " уже используется", "Ошибка при попытке присвоения email");
  }
}
