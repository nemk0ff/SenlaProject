package ru.senla.socialnetwork.exceptions.users;

public class UserNotRegisteredException extends UserException {
  public UserNotRegisteredException(String idOrEmail) {
    super("Пользователь " + idOrEmail + " не зарегистрирован", "Ошибка при поиске пользователя");
  }
}
