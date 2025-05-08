package ru.senla.socialnetwork.exceptions.users;

import lombok.Getter;
import ru.senla.socialnetwork.exceptions.SocialNetworkException;

@Getter
public class UserException extends SocialNetworkException {

  public UserException(String message, String action) {
    super(message, action);}

  public UserException(String message) {
    super(message, "Ошибка при действии с пользователем");
  }
}
