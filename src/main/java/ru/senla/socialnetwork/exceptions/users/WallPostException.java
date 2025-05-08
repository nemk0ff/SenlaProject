package ru.senla.socialnetwork.exceptions.users;

import ru.senla.socialnetwork.exceptions.SocialNetworkException;

public class WallPostException extends SocialNetworkException {
  public WallPostException(String message) {
    super(message, "Ошибка при действии с постом на стене пользователя");
  }

  public WallPostException(String message, String action) {
    super(message, action);
  }
}
