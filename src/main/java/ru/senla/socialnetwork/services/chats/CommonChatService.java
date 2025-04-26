package ru.senla.socialnetwork.services.chats;

import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;

public interface CommonChatService {
  Chat getChat(Long chatId);

  ChatMember getMember(Long chatId, String email);

  boolean isChatMember(Long chatId, String email);

  boolean isAdmin(Long chatId, String email);

  boolean isModerator(Long chatId, String email);
}
