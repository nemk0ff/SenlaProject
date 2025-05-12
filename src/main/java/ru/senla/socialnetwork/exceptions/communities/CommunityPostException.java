package ru.senla.socialnetwork.exceptions.communities;

import ru.senla.socialnetwork.exceptions.SocialNetworkException;

public class CommunityPostException extends SocialNetworkException {
  public CommunityPostException(String message) {
    super(message, "Ошибка при действии с постом сообщества");
  }
}
