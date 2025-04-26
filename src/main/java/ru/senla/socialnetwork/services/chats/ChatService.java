package ru.senla.socialnetwork.services.chats;

import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;
import ru.senla.socialnetwork.dto.chats.CreatePersonalChatDTO;

public interface ChatService {
  ChatDTO create(CreateGroupChatDTO chatDTO);

  ChatDTO create(CreatePersonalChatDTO chatDTO);

  void deleteChat(Long chatId);

  ChatDTO getChat(Long chatId);
}
