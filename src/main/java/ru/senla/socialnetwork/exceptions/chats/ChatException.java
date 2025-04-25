package ru.senla.socialnetwork.exceptions.chats;

import lombok.Getter;

@Getter
public class ChatException extends RuntimeException {
  private final String action;

  public ChatException(String message, String action) {
    super(message);
    this.action = action;
  }

  public ChatException(String message) {
    super(message);
    action = "Ошибка при действии с чатом";
  }
}
