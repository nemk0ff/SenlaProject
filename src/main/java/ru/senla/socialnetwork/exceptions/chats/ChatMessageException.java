package ru.senla.socialnetwork.exceptions.chats;

public class ChatMessageException extends ChatException {
  public ChatMessageException(String message) {
    super(message, "Ошибка при действии с сообщением в чате");
  }
}
