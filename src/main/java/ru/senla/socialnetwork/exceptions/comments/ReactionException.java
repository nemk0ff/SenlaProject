package ru.senla.socialnetwork.exceptions.comments;

import ru.senla.socialnetwork.exceptions.SocialNetworkException;

public class ReactionException extends SocialNetworkException {
  public ReactionException(String message) {
    super(message, "Ошибка при действии с реакцией");
  }

  public ReactionException(String message, String action) {
    super(message, action);
  }
}
