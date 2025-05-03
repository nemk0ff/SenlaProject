package ru.senla.socialnetwork.services.chats;

import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;
import ru.senla.socialnetwork.dto.chats.CreatePersonalChatDTO;
import ru.senla.socialnetwork.model.chats.Chat;

public interface ChatService {
  Chat create(CreateGroupChatDTO chatDTO);

  Chat create(CreatePersonalChatDTO chatDTO, String chatName);

  void delete(Chat chat);

  Chat get(Long chatId);
}
