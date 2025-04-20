package ru.senla.socialnetwork.exceptions.users;

public class UserNotRegisteredException extends IllegalArgumentException {
  public UserNotRegisteredException(String idOrEmail) {
    super("Пользователь " + idOrEmail + " не зарегистрирован");
  }
}
