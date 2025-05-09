package ru.senla.socialnetwork.exceptions.chats;

public class MessageException extends ChatException {
  public MessageException(String message) {
    super(message, "Ошибка при действии с сообщением в чате");
  }
}
