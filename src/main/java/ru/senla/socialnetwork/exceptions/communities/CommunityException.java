package ru.senla.socialnetwork.exceptions.communities;

import ru.senla.socialnetwork.exceptions.SocialNetworkException;

public class CommunityException extends SocialNetworkException {
  public CommunityException(String message) {
    super(message, "Ошибка при действии с сообществом");
  }

  public CommunityException(String message, String action) {
    super(message, action);
  }
}
