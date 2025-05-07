package ru.senla.socialnetwork.exceptions.comments;

import ru.senla.socialnetwork.exceptions.SocialNetworkException;

public class CommentException extends SocialNetworkException {
  public CommentException(String message) {
    super(message, "Ошибка при действии с комментарием");
  }

  public CommentException(String message, String action) {
    super(message, action);
  }
}
