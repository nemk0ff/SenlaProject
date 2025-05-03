package ru.senla.socialnetwork.facades.chats;

import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;
import ru.senla.socialnetwork.dto.chats.CreatePersonalChatDTO;

public interface ChatFacade {
  ChatDTO create(CreateGroupChatDTO chatDTO);

  ChatDTO create(CreatePersonalChatDTO chatDTO);

  void delete(Long chatId);

  ChatDTO get(Long chatId);
}
