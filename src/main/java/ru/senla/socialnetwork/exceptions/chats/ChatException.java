package ru.senla.socialnetwork.exceptions.chats;

import ru.senla.socialnetwork.exceptions.SocialNetworkException;

public class ChatException extends SocialNetworkException {
  public ChatException(String message) {
    super(message, "Ошибка при действии с чатом");
  }

  public ChatException(String message, String action) {
    super(message, action);
  }
}
