package ru.senla.socialnetwork.exceptions.chats;

public class ChatMemberException extends ChatException {
  public ChatMemberException(String message) {
    super(message, "Ошибка при действии с пользователем чата");
  }
}
