package ru.senla.socialnetwork.services.chats;

import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.dto.chats.CreateChatDTO;

public interface ChatService {
  ChatDTO create(CreateChatDTO chatDTO);

  void deleteChat(Long chatId);
}
